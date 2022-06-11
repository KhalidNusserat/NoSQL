package com.atypon.nosql.index;

import com.atypon.nosql.document.DocumentField;
import com.atypon.nosql.gsondocument.GsonDocument;
import com.atypon.nosql.io.DocumentsIO;
import com.atypon.nosql.utils.ExtraFileUtils;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import java.util.Set;

public class GsonDocumentFieldIndexManager implements FieldIndexManager<JsonElement, GsonDocument> {
    private final DocumentsIO<GsonDocument> documentsIO;

    private final Gson gson;

    private final Random random = new Random();

    public GsonDocumentFieldIndexManager(DocumentsIO<GsonDocument> documentsIO, Gson gson) {
        this.documentsIO = documentsIO;
        this.gson = gson;
    }

    private void indexAllDocuments(Path documentsPath, GsonDocumentFieldIndex fieldIndex) throws IOException {
        Files.walk(documentsPath, 1)
                .filter(ExtraFileUtils::isJsonFile)
                .forEach(path -> documentsIO.read(path).ifPresent(document -> fieldIndex.add(document, path)));
    }

    @Override
    public GsonDocumentFieldIndex loadFieldIndex(Path indexPath, Path documentsPath) {
        try (BufferedReader reader = Files.newBufferedReader(indexPath)) {
            Set<DocumentField> documentFields = gson.fromJson(reader, new TypeToken<Set<DocumentField>>(){}.getType());
            GsonDocumentFieldIndex fieldIndex = new GsonDocumentFieldIndex(documentFields, indexPath);
            indexAllDocuments(documentsPath, fieldIndex);
            return fieldIndex;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public GsonDocumentFieldIndex createNewFieldIndex(
            Set<DocumentField> documentFields, Path documentsPath, Path indexPath) {
        GsonDocumentFieldIndex fieldIndex = new GsonDocumentFieldIndex(documentFields, indexPath);
        try {
            indexAllDocuments(documentsPath, fieldIndex);
            return fieldIndex;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Path writeFieldIndex(Set<DocumentField> documentFields, Path indexesPath) {
        Path indexPath = indexesPath.resolve(random.nextLong() + ".json");
        try (BufferedWriter writer = Files.newBufferedWriter(indexPath)) {
            gson.toJson(documentFields, writer);
            return indexPath;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeFieldIndex(Path indexPath) {
        documentsIO.delete(indexPath);
    }
}

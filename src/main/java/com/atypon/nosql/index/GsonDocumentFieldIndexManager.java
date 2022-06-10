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
import java.util.Optional;
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

    @Override
    public GsonDocumentFieldIndex createFromStoredFieldIndex(Path indexPath, Path documentsPath) {
        try (BufferedReader reader = Files.newBufferedReader(indexPath)) {
            Set<DocumentField> documentFields = gson.fromJson(reader, new TypeToken<Set<DocumentField>>(){}.getType());
            GsonDocumentFieldIndex fieldIndex = new GsonDocumentFieldIndex(documentFields);
            Files.walk(documentsPath, 1)
                    .filter(ExtraFileUtils::isJsonFile)
                    .forEach(path -> {
                        Optional<GsonDocument> document = documentsIO.read(path);
                        document.ifPresent(gsonDocument -> fieldIndex.add(gsonDocument, path));
                    });
            return fieldIndex;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public GsonDocumentFieldIndex createNewFieldIndex(
            Set<DocumentField> documentFields, Path collectionsPath, Path indexesPath) {
        GsonDocumentFieldIndex gsonFieldIndex = new GsonDocumentFieldIndex(documentFields);
        Path indexPath = indexesPath.resolve(random.nextLong() + ".index");
        try (BufferedWriter writer = Files.newBufferedWriter(indexPath)) {
            gson.toJson(documentFields, writer);
            Files.walk(collectionsPath, 1)
                    .filter(ExtraFileUtils::isJsonFile)
                    .forEach(path -> documentsIO.read(path).ifPresent(document -> gsonFieldIndex.add(document, path)));
            return gsonFieldIndex;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

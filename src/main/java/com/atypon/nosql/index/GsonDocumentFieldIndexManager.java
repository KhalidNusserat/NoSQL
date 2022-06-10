package com.atypon.nosql.index;

import com.atypon.nosql.document.DocumentField;
import com.atypon.nosql.gsondocument.GsonDocument;
import com.atypon.nosql.io.DocumentsIO;
import com.atypon.nosql.utils.ExtraFileUtils;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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

    @Override
    public GsonDocumentFieldIndex read(Path indexPath) {
        try (BufferedReader reader = Files.newBufferedReader(indexPath)) {
            return gson.fromJson(reader, GsonDocumentFieldIndex.class);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't read index file: " + indexPath);
        }
    }

    @Override
    public GsonDocumentFieldIndex create(Set<DocumentField> documentFields, Path collectionsPath, Path indexesPath) {
        Path indexPath = indexesPath.resolve(random.nextLong() + ".index");
        GsonDocumentFieldIndex gsonFieldIndex = new GsonDocumentFieldIndex(documentFields, indexPath);
        try {
            Files.walk(collectionsPath, 1)
                    .filter(ExtraFileUtils::isJsonFile)
                    .forEach(path -> {
                        documentsIO.read(path).ifPresent(document -> gsonFieldIndex.add(document, path));
                    });
            return gsonFieldIndex;
        } catch (IOException e) {
            throw new RuntimeException("Could not access the directory: " + collectionsPath);
        }
    }

    @Override
    public void update(FieldIndex<JsonElement, GsonDocument> fieldIndex) {
        try (BufferedWriter writer = Files.newBufferedWriter(fieldIndex.getPath())) {
            gson.toJson(fieldIndex, writer);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't open the file: " + fieldIndex.getPath());
        }
    }
}

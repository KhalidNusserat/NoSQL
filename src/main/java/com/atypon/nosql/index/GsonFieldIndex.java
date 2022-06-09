package com.atypon.nosql.index;

import com.atypon.nosql.document.DocumentField;
import com.atypon.nosql.gsondocument.GsonDocument;
import com.atypon.nosql.gsondocument.GsonDocumentParser;
import com.atypon.nosql.io.DocumentsIO;
import com.atypon.nosql.io.GsonDocumentsIO;
import com.atypon.nosql.utils.ExtraFileUtils;
import com.atypon.nosql.utils.ReversedHashMap;
import com.atypon.nosql.utils.ReversedMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class GsonFieldIndex implements FieldIndex<JsonElement, GsonDocument> {
    private final Set<DocumentField> documentFields;

    private final ReversedMap<Path, Set<JsonElement>> pathToValues;

    private final DocumentsIO<GsonDocument> documentsIO;

    private Path indexPath;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private final Gson gson;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public GsonFieldIndex(
            Set<DocumentField> documentFields,
            Path indexPath,
            Gson gson
    ) {
        this.documentFields = documentFields;
        this.indexPath = indexPath;
        this.documentsIO = new GsonDocumentsIO(new GsonDocumentParser());
        this.gson = gson;
        this.pathToValues = new ReversedHashMap<>();
    }

    public GsonFieldIndex(
            Set<DocumentField> documentFields,
            Path indexPath,
            Gson gson,
            ReversedMap<Path, Set<JsonElement>> pathToValues
    ) {
        this.documentFields = documentFields;
        this.indexPath = indexPath;
        this.documentsIO = new GsonDocumentsIO(new GsonDocumentParser());
        this.gson = gson;
        this.pathToValues = pathToValues;
    }

    @Override
    public void initialize(Path directoryPath) {
        try {
            Files.walk(directoryPath)
                    .filter(ExtraFileUtils::isJsonFile)
                    .forEach(path -> documentsIO.read(path).ifPresent(document -> add(document, path)));
        } catch (IOException e) {
            throw new RuntimeException("Couldn't initialize index: " + documentFields);
        }
    }

    @Override
    public void add(GsonDocument document, Path documentPath) {
        Set<JsonElement> values = document.getAll(documentFields);
        pathToValues.put(documentPath, values);
        updateMap();
    }

    private void updateMap() {
        executorService.submit(() -> {
            JsonObject object = new JsonObject();
            object.add("_content", gson.toJsonTree(pathToValues).getAsJsonObject());
            object.add("_fields", gson.toJsonTree(documentFields).getAsJsonObject());
            try {
                indexPath = documentsIO.update(GsonDocument.of(object), indexPath);
            } catch (IOException e) {
                throw new RuntimeException("Could not update index at: " + indexPath);
            }
        });
    }

    @Override
    public void remove(GsonDocument document) {
        pathToValues.removeByValue(document.getAll(documentFields));
        updateMap();
    }

    @Override
    public Collection<Path> get(GsonDocument matchDocument) {
        return pathToValues.getFromValue(matchDocument.getAll(documentFields));
    }

    @Override
    public Set<DocumentField> getDocumentFields() {
        return documentFields;
    }

    @Override
    public boolean contains(GsonDocument matchDocument) {
        return pathToValues.containsValue(matchDocument.getAll(documentFields));
    }
}

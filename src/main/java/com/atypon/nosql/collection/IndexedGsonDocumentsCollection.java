package com.atypon.nosql.collection;

import com.atypon.nosql.document.DocumentField;
import com.atypon.nosql.gsondocument.GsonDocument;
import com.atypon.nosql.index.FieldIndex;
import com.atypon.nosql.index.FieldIndexManager;
import com.atypon.nosql.io.DocumentsIO;
import com.atypon.nosql.utils.ExtraFileUtils;
import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class IndexedGsonDocumentsCollection implements IndexedDocumentsCollection<GsonDocument> {
    private final DefaultGsonDocumentsCollection documentsCollection;

    private final DocumentsIO<GsonDocument> documentsIO;

    private final Path documentsPath;

    private final Map<Set<DocumentField>, FieldIndex<JsonElement, GsonDocument>> fieldIndexes =
            new ConcurrentHashMap<>();

    private final FieldIndexManager<JsonElement, GsonDocument> fieldIndexManager;

    private final Path indexesPath;

    private void loadAllFieldIndexes(
            Path collectionsPath,
            FieldIndexManager<JsonElement, GsonDocument> fieldIndexManager
    ) throws IOException {
        Files.walk(indexesPath)
                .filter(ExtraFileUtils::isJsonFile)
                .map(path -> fieldIndexManager.loadFieldIndex(path, collectionsPath))
                .forEach(fieldIndex -> fieldIndexes.put(fieldIndex.getDocumentFields(), fieldIndex));
    }

    public IndexedGsonDocumentsCollection(
            DocumentsIO<GsonDocument> documentsIO,
            Path collectionsPath,
            FieldIndexManager<JsonElement, GsonDocument> fieldIndexManager
    ) {
        this.documentsIO = documentsIO;
        this.documentsPath = collectionsPath;
        this.documentsCollection = new DefaultGsonDocumentsCollection(documentsIO, collectionsPath);
        this.fieldIndexManager = fieldIndexManager;
        indexesPath = collectionsPath.resolve("indexes/");
        try {
            Files.createDirectories(indexesPath);
            loadAllFieldIndexes(collectionsPath, fieldIndexManager);
            Set<DocumentField> idField = Set.of(DocumentField.of("_matchID"));
            if (!fieldIndexes.containsKey(idField)) {
                createIndex(idField);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createIndex(Set<DocumentField> documentFields) {
        Path indexPath = fieldIndexManager.writeFieldIndex(documentFields, indexesPath);
        fieldIndexes.put(
                documentFields,
                fieldIndexManager.createNewFieldIndex(documentFields, documentsPath, indexPath)
        );
    }

    @Override
    public void deleteIndex(Set<DocumentField> documentFields) {
        documentsIO.delete(fieldIndexes.get(documentFields).getPath());
        fieldIndexes.remove(documentFields);
    }

    @Override
    public Collection<Set<DocumentField>> getIndexes() {
        return fieldIndexes.keySet();
    }

    @NotNull
    private Set<DocumentField> getDocumentFields(GsonDocument matchDocument) {
        Set<DocumentField> documentFields = matchDocument.getFields();
        documentFields.remove(DocumentField.of("_id"));
        return documentFields;
    }

    @Override
    public boolean contains(GsonDocument matchDocument) {
        Set<DocumentField> documentFields = getDocumentFields(matchDocument);
        if (fieldIndexes.containsKey(documentFields)) {
            return fieldIndexes.get(documentFields).contains(matchDocument);
        } else {
            return documentsCollection.contains(matchDocument);
        }
    }

    @Override
    public Collection<GsonDocument> getAllThatMatches(GsonDocument matchDocument) {
        Set<DocumentField> documentFields = getDocumentFields(matchDocument);
        if (fieldIndexes.containsKey(documentFields)) {
            return fieldIndexes.get(documentFields)
                    .get(matchDocument)
                    .stream()
                    .map(Path::of)
                    .map(documentsIO::read)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
        } else {
            return documentsCollection.getAllThatMatches(matchDocument);
        }
    }

    @Override
    public Path put(GsonDocument document) throws IOException {
        Path documentPath;
        if (contains((GsonDocument) document.matchID())) {
            Path oldDocumentPath = fieldIndexes.get(Set.of(DocumentField.of("_matchID")))
                    .get((GsonDocument) document.matchID())
                    .stream()
                    .findFirst()
                    .map(Path::of)
                    .orElseThrow();
            documentPath = documentsIO.update(document, oldDocumentPath);
        } else {
            documentPath = documentsIO.write(document, documentsPath);
        }
        fieldIndexes.forEach(((documentFields, fieldIndex) -> fieldIndex.add(document, documentPath)));
        return documentPath;
    }

    @Override
    public void deleteAllThatMatches(GsonDocument matchDocument) throws IOException {
        Set<DocumentField> documentFields = getDocumentFields(matchDocument);
        if (fieldIndexes.containsKey(documentFields)) {
            fieldIndexes.get(documentFields).get(matchDocument)
                    .forEach(pathString -> documentsIO.delete(Path.of(pathString)));
            fieldIndexes.forEach(((fields, fieldIndex) -> fieldIndex.remove(matchDocument)));
        } else {
            documentsCollection.deleteAllThatMatches(matchDocument);
        }
    }

    @Override
    public Collection<GsonDocument> getAll() {
        return documentsCollection.getAll();
    }
}

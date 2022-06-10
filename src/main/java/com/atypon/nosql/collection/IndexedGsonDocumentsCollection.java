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

public class IndexedGsonDocumentsCollection implements DocumentsCollection<GsonDocument> {
    private final DefaultGsonDocumentsCollection documentsCollection;

    private final DocumentsIO<GsonDocument> documentsIO;

    private final Path directoryPath;

    private final Map<Set<DocumentField>, FieldIndex<JsonElement, GsonDocument>> fieldIndexes =
            new ConcurrentHashMap<>();

    private final FieldIndexManager<JsonElement, GsonDocument> fieldIndexManager;

    public IndexedGsonDocumentsCollection(
            DocumentsIO<GsonDocument> documentsIO,
            Path collectionsPath,
            FieldIndexManager<JsonElement, GsonDocument> fieldIndexManager
    ) {
        this.documentsIO = documentsIO;
        this.directoryPath = collectionsPath;
        Path indexesPath = collectionsPath.resolve("indexes/");
        this.documentsCollection = DefaultGsonDocumentsCollection.from(documentsIO, collectionsPath);
        this.fieldIndexManager = fieldIndexManager;
        try {
            Files.createDirectories(indexesPath);
            Files.walk(indexesPath)
                    .filter(ExtraFileUtils::isIndexFile)
                    .map(fieldIndexManager::read)
                    .filter(Objects::nonNull)
                    .forEach(fieldIndex -> fieldIndexes.put(fieldIndex.getDocumentFields(), fieldIndex));
            Set<DocumentField> idField = Set.of(DocumentField.of("_matchID"));
            if (!fieldIndexes.containsKey(idField)) {
                fieldIndexes.put(idField, fieldIndexManager.create(idField, collectionsPath, indexesPath));
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not access the indexes folder at: " + indexesPath);
        }
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
            documentPath = documentsIO.write(document, directoryPath);
        }
        fieldIndexes.forEach(((documentFields, fieldIndex) -> {
            fieldIndex.add(document, documentPath);
            fieldIndexManager.update(fieldIndex);
        }));
        return documentPath;
    }

    @Override
    public void remove(GsonDocument matchDocument) throws IOException {
        Set<DocumentField> documentFields = getDocumentFields(matchDocument);
        if (fieldIndexes.containsKey(documentFields)) {
            fieldIndexes.get(documentFields).get(matchDocument)
                    .forEach(pathString -> documentsIO.delete(Path.of(pathString)));
            fieldIndexes.forEach(((fields, fieldIndex) -> {
                fieldIndex.remove(matchDocument);
                fieldIndexManager.update(fieldIndex);
            }));
        } else {
            documentsCollection.remove(matchDocument);
        }
    }

    @Override
    public Collection<GsonDocument> getAll() {
        return documentsCollection.getAll();
    }
}

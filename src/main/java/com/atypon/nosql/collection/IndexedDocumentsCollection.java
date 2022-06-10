package com.atypon.nosql.collection;

import com.atypon.nosql.document.Document;
import com.atypon.nosql.document.DocumentField;
import com.atypon.nosql.index.FieldIndexManager;
import com.atypon.nosql.index.FieldIndex;
import com.atypon.nosql.io.DocumentsIO;
import com.atypon.nosql.utils.ExtraFileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class IndexedDocumentsCollection<E, T extends Document<E>> implements DocumentsCollection<T> {
    private final DefaultDocumentsCollection<E, T> documentsCollection;

    private final DocumentsIO<T> documentsIO;

    private final Path directoryPath;

    private final Map<Set<DocumentField>, FieldIndex<E, T>> fieldIndexes = new ConcurrentHashMap<>();

    private final FieldIndexManager<E, T> fieldIndexManager;

    @NotNull
    private Set<DocumentField> getDocumentFields(T matchDocument) {
        Set<DocumentField> documentFields = matchDocument.getFields();
        documentFields.remove(DocumentField.of("_id"));
        return documentFields;
    }

    public IndexedDocumentsCollection(
            DocumentsIO<T> documentsIO,
            Path collectionsPath,
            FieldIndexManager<E, T> fieldIndexManager
    ) {
        this.documentsIO = documentsIO;
        this.directoryPath = collectionsPath;
        Path indexesPath = collectionsPath.resolve("indexes/");
        this.documentsCollection = DefaultDocumentsCollection.from(documentsIO, collectionsPath);
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

    @Override
    public boolean contains(T matchDocument) throws IOException {
        Set<DocumentField> documentFields = getDocumentFields(matchDocument);
        if (fieldIndexes.containsKey(documentFields)) {
            return fieldIndexes.get(documentFields).contains(matchDocument);
        } else {
            return documentsCollection.contains(matchDocument);
        }
    }

    @Override
    public Collection<T> getAllThatMatches(T matchDocument) throws IOException {
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
    @SuppressWarnings("unchecked")
    public Path put(T document) throws IOException {
        Path documentPath;
        if (contains((T) document.matchID())) {
            Path oldDocumentPath = fieldIndexes.get(Set.of(DocumentField.of("_matchID")))
                    .get((T) document.matchID())
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
    public void remove(T matchDocument) throws IOException {
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
    public Collection<T> getAll() throws IOException {
        return documentsCollection.getAll();
    }
}

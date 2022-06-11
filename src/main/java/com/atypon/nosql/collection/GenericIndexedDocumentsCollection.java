package com.atypon.nosql.collection;

import com.atypon.nosql.document.Document;
import com.atypon.nosql.document.DocumentGenerator;
import com.atypon.nosql.gsondocument.FieldsDoNotMatchException;
import com.atypon.nosql.index.Index;
import com.atypon.nosql.index.IndexGenerator;
import com.atypon.nosql.io.IOEngine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GenericIndexedDocumentsCollection<T extends Document<?>> implements IndexedDocumentsCollection<T> {
    private final GenericDefaultDocumentsCollection<T> documentsCollection;

    private final IOEngine ioEngine;

    private final GenericDefaultDocumentsCollection<T> indexesCollection;

    private final DocumentGenerator<T> documentGenerator;

    private final Map<T, Index<T>> indexes = new ConcurrentHashMap<>();

    private final IndexGenerator<T> indexGenerator;

    private void createIdIndex() throws IOException {
        String idCriteriaFields = "{\"_id\": null}";
        T idCriteria = documentGenerator.createFromString(idCriteriaFields);
        if (!indexes.containsKey(idCriteria)) {
            Path indexPath = indexesCollection.addDocument(idCriteria);
            indexes.put(
                    idCriteria,
                    indexGenerator.createNewIndex(idCriteria, indexPath, ioEngine, documentGenerator)
            );
        }
    }

    public GenericIndexedDocumentsCollection(
            Path collectionPath,
            DocumentGenerator<T> documentGenerator,
            IndexGenerator<T> indexGenerator,
            IOEngine ioEngine
    ) {
        this.ioEngine = ioEngine;
        this.documentGenerator = documentGenerator;
        this.indexGenerator = indexGenerator;
        this.documentsCollection = new GenericDefaultDocumentsCollection<>(ioEngine, collectionPath, documentGenerator);
        indexesCollection =  new GenericDefaultDocumentsCollection<>(
                ioEngine,
                collectionPath.resolve("indexes/"),
                documentGenerator
        );
        try {
            Files.createDirectories(collectionPath.resolve("indexes/"));
            createIdIndex();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createIndex(T indexFields) throws IOException {
        Path indexPath = indexesCollection.addDocument(indexFields);
        indexes.put(
                indexFields,
                indexGenerator.createNewIndex(indexFields, indexPath, ioEngine, documentGenerator)
        );
    }

    @Override
    public void deleteIndex(T indexFields) throws NoSuchIndexException {
        if (!indexes.containsKey(indexFields)) {
            throw new NoSuchIndexException(indexFields);
        }
        ioEngine.delete(indexes.get(indexFields).getIndexPath());
        indexes.remove(indexFields);
    }

    @Override
    public Collection<T> getIndexes() {
        return indexesCollection.getAll();
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean contains(T documentCriteria) throws FieldsDoNotMatchException {
        T criteriaFields = (T) documentCriteria.getFields();
        if (indexes.containsKey(criteriaFields)) {
            Index<T> index = indexes.get(criteriaFields);
            return index.contains(
                    (T) index.getFields().getValuesToMatch(documentCriteria)
            );
        } else {
            return documentsCollection.contains(documentCriteria);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<T> getAllThatMatches(T documentCriteria) throws FieldsDoNotMatchException {
        T criteriaFields = (T) documentCriteria.getFields();
        if (indexes.containsKey(criteriaFields)) {
            Index<T> index = indexes.get(criteriaFields);
            return index.get((T) documentCriteria.getValuesToMatch(index.getFields()))
                    .stream()
                    .map(documentPath -> ioEngine.read(documentPath, documentGenerator))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
        } else {
            return documentsCollection.getAllThatMatches(documentCriteria);
        }
    }

    private void updateAllIndexes(T document, Path documentPath) {
        indexes.values().forEach(index -> {
            try {
                index.add(document, documentPath);
            } catch (FieldsDoNotMatchException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public Path addDocument(T addedDocument) throws IOException {
        Path addedDocumentPath = documentsCollection.addDocument(addedDocument);
        updateAllIndexes(addedDocument, addedDocumentPath);
        return addedDocumentPath;
    }

    @SuppressWarnings("unchecked")
    private List<Path> getMatchingDocumentPath(T documentCriteria) {
        T criteriaFields = (T) documentCriteria.getFields();
        try {
            return indexes.get(criteriaFields)
                    .get(documentCriteria)
                    .stream()
                    .filter(path -> ioEngine.read(path, documentGenerator)
                            .map(documentCriteria::subsetOf)
                            .orElse(false))
                    .toList();
        } catch (FieldsDoNotMatchException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Path updateDocument(T oldDocument, T updatedDocument)
            throws NoSuchDocumentException, MultipleFilesMatchedException, IOException {
        List<Path> matchingDocumentsPath = getMatchingDocumentPath(oldDocument);
        if (matchingDocumentsPath.size() > 1) {
            throw new MultipleFilesMatchedException(matchingDocumentsPath.size());
        } else if (matchingDocumentsPath.size() == 0) {
            throw new NoSuchDocumentException(oldDocument);
        }
        else {
            Path updatedDocumentPath = documentsCollection.updateDocument(oldDocument, updatedDocument);
            updateAllIndexes(updatedDocument, updatedDocumentPath);
            return updatedDocumentPath;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void deleteAllThatMatches(T documentCriteria) throws FieldsDoNotMatchException {
        T criteriaFields = (T) documentCriteria.getFields();
        if (indexes.containsKey(criteriaFields)) {
            indexes.get(criteriaFields).get(documentCriteria).forEach(ioEngine::delete);
            indexes.forEach((fields, fieldIndex) -> {
                try {
                    fieldIndex.remove(documentCriteria);
                } catch (FieldsDoNotMatchException e) {
                    e.printStackTrace();
                }});
        } else {
            documentsCollection.deleteAllThatMatches(documentCriteria);
        }
    }

    @Override
    public Collection<T> getAll() {
        return documentsCollection.getAll();
    }
}

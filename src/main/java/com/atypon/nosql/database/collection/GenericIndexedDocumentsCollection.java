package com.atypon.nosql.database.collection;

import com.atypon.nosql.database.document.Document;
import com.atypon.nosql.database.document.DocumentGenerator;
import com.atypon.nosql.database.gsondocument.FieldsDoNotMatchException;
import com.atypon.nosql.database.index.Index;
import com.atypon.nosql.database.index.IndexGenerator;
import com.atypon.nosql.database.io.IOEngine;
import com.atypon.nosql.database.utils.ExtraFileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class GenericIndexedDocumentsCollection<T extends Document<?>> implements IndexedDocumentsCollection<T> {
    private final IOEngine<T> ioEngine;

    private final Path documentsPath;

    private final GenericBasicDocumentsCollection<T> documentsCollection;

    private final DocumentGenerator<T> documentGenerator;

    private final GenericBasicDocumentsCollection<T> indexesCollection;

    private final Map<T, Index<T>> indexes = new ConcurrentHashMap<>();

    private final IndexGenerator<T> indexGenerator;

    private final Path indexesPath;

    public GenericIndexedDocumentsCollection(
            Path collectionPath,
            DocumentGenerator<T> documentGenerator,
            IndexGenerator<T> indexGenerator,
            IOEngine<T> ioEngine
    ) {
        this.ioEngine = ioEngine;
        this.documentGenerator = documentGenerator;
        this.indexGenerator = indexGenerator;
        documentsPath = collectionPath.resolve("documents/");
        documentsCollection = GenericBasicDocumentsCollection.<T>builder()
                .setIoEngine(ioEngine)
                .setDocumentsGenerator(documentGenerator)
                .setDocumentsPath(documentsPath)
                .build();
        indexesPath = collectionPath.resolve("indexes/");
        indexesCollection = GenericBasicDocumentsCollection.<T>builder()
                .setIoEngine(ioEngine)
                .setDocumentsGenerator(documentGenerator)
                .setDocumentsPath(indexesPath)
                .build();
        try {
            Files.createDirectories(indexesPath);
            loadIndexes();
            createIdIndex();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T extends Document<?>> GenericIndexedDocumentsCollectionBuilder<T> builder() {
        return new GenericIndexedDocumentsCollectionBuilder<>();
    }

    private void loadIndexes() throws IOException {
        Files.walk(indexesPath)
                .filter(ExtraFileUtils::isJsonFile)
                .forEach(indexPath -> {
                    T indexFields = ioEngine.read(indexPath, documentGenerator).orElseThrow();
                    Index<T> index = indexGenerator.createNewIndex(indexFields, indexPath, ioEngine, documentGenerator);
                    index.populateIndex(documentsPath);
                    indexes.put(indexFields, index);
                });
    }

    private void createIdIndex() {
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

    @Override
    public void createIndex(T indexFields) {
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
    public Path addDocument(T addedDocument) {
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
    public Path updateDocument(T documentCriteria, T updatedDocument)
            throws NoSuchDocumentException, MultipleFilesMatchedException {
        List<Path> matchingDocumentsPath = getMatchingDocumentPath(documentCriteria);
        if (matchingDocumentsPath.size() > 1) {
            throw new MultipleFilesMatchedException(matchingDocumentsPath.size());
        } else if (matchingDocumentsPath.size() == 0) {
            throw new NoSuchDocumentException(documentCriteria);
        } else {
            Path updatedDocumentPath = documentsCollection.updateDocument(documentCriteria, updatedDocument);
            updateAllIndexes(updatedDocument, updatedDocumentPath);
            return updatedDocumentPath;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public int deleteAllThatMatches(T documentCriteria) throws FieldsDoNotMatchException {
        T criteriaFields = (T) documentCriteria.getFields();
        if (indexes.containsKey(criteriaFields)) {
            Collection<Path> paths = indexes.get(criteriaFields).get(documentCriteria);
            paths.forEach(ioEngine::delete);
            indexes.forEach((fields, fieldIndex) -> {
                try {
                    fieldIndex.remove(documentCriteria);
                } catch (FieldsDoNotMatchException e) {
                    e.printStackTrace();
                }
            });
            return paths.size();
        } else {
            return documentsCollection.deleteAllThatMatches(documentCriteria);
        }
    }

    @Override
    public Collection<T> getAll() {
        return documentsCollection.getAll();
    }

    public static class GenericIndexedDocumentsCollectionBuilder<T extends Document<?>> {
        private Path documentsPath;

        private DocumentGenerator<T> documentGenerator;

        private IndexGenerator<T> indexGenerator;

        private IOEngine<T> ioEngine;

        public GenericIndexedDocumentsCollectionBuilder<T> setDocumentsPath(Path documentsPath) {
            this.documentsPath = documentsPath;
            return this;
        }

        public GenericIndexedDocumentsCollectionBuilder<T> setDocumentGenerator(
                DocumentGenerator<T> documentGenerator) {
            this.documentGenerator = documentGenerator;
            return this;
        }

        public GenericIndexedDocumentsCollectionBuilder<T> setIndexGenerator(IndexGenerator<T> indexGenerator) {
            this.indexGenerator = indexGenerator;
            return this;
        }

        public GenericIndexedDocumentsCollectionBuilder<T> setIOEngine(IOEngine<T> ioEngine) {
            this.ioEngine = ioEngine;
            return this;
        }

        public GenericIndexedDocumentsCollection<T> create() {
            return new GenericIndexedDocumentsCollection<>(
                    documentsPath,
                    documentGenerator,
                    indexGenerator,
                    ioEngine
            );
        }
    }
}

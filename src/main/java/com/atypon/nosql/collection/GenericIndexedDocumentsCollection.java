package com.atypon.nosql.collection;

import com.atypon.nosql.document.Document;
import com.atypon.nosql.document.DocumentGenerator;
import com.atypon.nosql.gsondocument.FieldsDoNotMatchException;
import com.atypon.nosql.index.Index;
import com.atypon.nosql.index.IndexGenerator;
import com.atypon.nosql.io.IOEngine;
import com.atypon.nosql.utils.ExtraFileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class GenericIndexedDocumentsCollection<T extends Document<?>> implements IndexedDocumentsCollection<T> {
    private final Path documentsPath;

    private final Path indexesPath;

    private final GenericDefaultDocumentsCollection<T> documentsCollection;

    private final IOEngine ioEngine;

    private final GenericDefaultDocumentsCollection<T> indexesCollection;

    private final DocumentGenerator<T> documentGenerator;

    private final Map<T, Index<T>> indexes = new ConcurrentHashMap<>();

    private final IndexGenerator<T> indexGenerator;

    public GenericIndexedDocumentsCollection(
            Path documentsPath,
            DocumentGenerator<T> documentGenerator,
            IndexGenerator<T> indexGenerator,
            IOEngine ioEngine
    ) {
        this.ioEngine = ioEngine;
        this.documentGenerator = documentGenerator;
        this.indexGenerator = indexGenerator;
        this.documentsCollection = new GenericDefaultDocumentsCollection<>(ioEngine, documentsPath, documentGenerator);
        this.documentsPath = documentsPath;
        indexesPath = documentsPath.resolve("indexes/");
        indexesCollection = new GenericDefaultDocumentsCollection<>(
                ioEngine,
                indexesPath,
                documentGenerator
        );
        try {
            Files.createDirectories(documentsPath.resolve("indexes/"));
            loadIndexes();
            createIdIndex();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        } else {
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
                }
            });
        } else {
            documentsCollection.deleteAllThatMatches(documentCriteria);
        }
    }

    @Override
    public Collection<T> getAll() {
        return documentsCollection.getAll();
    }

    public static <T extends Document<?>> GenericIndexedDocumentsCollectionBuilder<T> builder() {
        return new GenericIndexedDocumentsCollectionBuilder<>();
    }

    public static class GenericIndexedDocumentsCollectionBuilder<T extends Document<?>> {
        private Path documentsPath;

        private DocumentGenerator<T> documentGenerator;

        private IndexGenerator<T> indexGenerator;

        private IOEngine ioEngine;

        public GenericIndexedDocumentsCollectionBuilder<T> setDocumentsPath(Path documentsPath) {
            this.documentsPath = documentsPath;
            return this;
        }

        public GenericIndexedDocumentsCollectionBuilder<T> setDocumentGenerator(
                DocumentGenerator<T> documentGenerator)
        {
            this.documentGenerator = documentGenerator;
            return this;
        }

        public GenericIndexedDocumentsCollectionBuilder<T> setIndexGenerator(IndexGenerator<T> indexGenerator) {
            this.indexGenerator = indexGenerator;
            return this;
        }

        public GenericIndexedDocumentsCollectionBuilder<T> setIOEngine(IOEngine ioEngine) {
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

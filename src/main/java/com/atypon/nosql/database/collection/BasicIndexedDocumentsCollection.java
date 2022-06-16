package com.atypon.nosql.database.collection;

import com.atypon.nosql.database.document.Document;
import com.atypon.nosql.database.document.DocumentFactory;
import com.atypon.nosql.database.gsondocument.FieldsDoNotMatchException;
import com.atypon.nosql.database.index.Index;
import com.atypon.nosql.database.index.IndexFactory;
import com.atypon.nosql.database.io.IOEngine;
import com.atypon.nosql.database.utils.FileUtils;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class BasicIndexedDocumentsCollection implements IndexedDocumentsCollection {
    private final IOEngine ioEngine;

    private final Path documentsPath;

    private final BasicDocumentsCollection documentsCollection;

    private final DocumentFactory documentFactory;

    private final BasicDocumentsCollection indexesCollection;

    private final Map<Document, Index> indexes = new ConcurrentHashMap<>();

    private final IndexFactory indexFactory;

    private final Path indexesPath;

    private BasicIndexedDocumentsCollection(
            Path collectionPath,
            DocumentFactory documentFactory,
            IndexFactory indexFactory,
            IOEngine ioEngine
    ) {
        this.ioEngine = ioEngine;
        this.documentFactory = documentFactory;
        this.indexFactory = indexFactory;
        documentsPath = collectionPath.resolve("documents/");
        documentsCollection = new BasicDocumentsCollection(documentsPath, ioEngine);
        indexesPath = collectionPath.resolve("indexes/");
        indexesCollection = new BasicDocumentsCollection(indexesPath, ioEngine);
        FileUtils.createDirectories(indexesPath);
        loadIndexes();
        createIdIndex();
    }

    public static GenericIndexedDocumentsCollectionBuilder builder() {
        return new GenericIndexedDocumentsCollectionBuilder();
    }

    private void loadIndexes() {
        FileUtils.traverseDirectory(indexesPath)
                .filter(FileUtils::isJsonFile)
                .forEach(this::loadIndex);
    }

    private void loadIndex(Path indexPath) {
        Document indexFields = ioEngine.read(indexPath).orElseThrow();
        Index index = indexFactory.createNewIndex(indexFields, indexPath, ioEngine);
        index.populateIndex(documentsPath);
        indexes.put(indexFields, index);
    }

    private void createIdIndex() {
        String idCriteriaFields = "{\"_id\": null}";
        Document idCriteria = documentFactory.createFromString(idCriteriaFields);
        if (!indexes.containsKey(idCriteria)) {
            Path indexPath = indexesCollection.addDocument(idCriteria);
            indexes.put(
                    idCriteria,
                    indexFactory.createNewIndex(idCriteria, indexPath, ioEngine)
            );
        }
    }

    @Override
    public void createIndex(Document indexDocument) {
        if (indexes.containsKey(indexDocument)) {
            throw new IndexAlreadyExistsException(indexDocument);
        }
        Path indexPath = indexesCollection.addDocument(indexDocument);
        indexes.put(
                indexDocument,
                indexFactory.createNewIndex(indexDocument, indexPath, ioEngine)
        );
    }

    @Override
    public void deleteIndex(Document indexDocument) {
        if (!indexes.containsKey(indexDocument)) {
            throw new NoSuchIndexException(indexDocument);
        }
        ioEngine.delete(indexes.get(indexDocument).getIndexPath());
        indexes.remove(indexDocument);
    }

    @Override
    public boolean containsIndex(Document indexDocument) {
        return indexesCollection.contains(indexDocument);
    }

    @Override
    public Collection<Document> getIndexes() {
        return indexesCollection.getAll();
    }

    @Override
    public boolean contains(Document documentCriteria) throws FieldsDoNotMatchException {
        Document criteriaFields = documentCriteria.getFields();
        if (indexes.containsKey(criteriaFields)) {
            Index index = indexes.get(criteriaFields);
            return index.contains(
                    index.getFields().getValuesToMatch(documentCriteria)
            );
        } else {
            return documentsCollection.contains(documentCriteria);
        }
    }

    @Override
    public List<Document> getAllThatMatch(Document documentCriteria) {
        Document criteriaFields = documentCriteria.getFields();
        if (indexes.containsKey(criteriaFields)) {
            Index index = indexes.get(criteriaFields);
            return index.get(documentCriteria.getValuesToMatch(index.getFields()))
                    .stream()
                    .map(ioEngine::read)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
        } else {
            return documentsCollection.getAllThatMatch(documentCriteria);
        }
    }

    private void updateAllIndexes(Document document, Path documentPath) {
        indexes.values().forEach(index -> index.add(document, documentPath));
    }

    @Override
    public Path addDocument(Document addedDocument) {
        Path addedDocumentPath = documentsCollection.addDocument(addedDocument);
        updateAllIndexes(addedDocument, addedDocumentPath);
        return addedDocumentPath;
    }

    private List<Path> getMatchingDocumentPath(Document documentCriteria) {
        Document criteriaFields = documentCriteria.getFields();
        return indexes.get(criteriaFields)
                .get(documentCriteria)
                .stream()
                .filter(path -> ioEngine.read(path).map(documentCriteria::subsetOf).orElseThrow())
                .toList();
    }

    @Override
    public Path updateDocument(Document documentCriteria, Document updatedDocument) {
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
    public int removeAllThatMatch(Document documentCriteria) {
        Document criteriaFields = documentCriteria.getFields();
        if (indexes.containsKey(criteriaFields)) {
            Collection<Path> paths = indexes.get(criteriaFields).get(documentCriteria);
            paths.forEach(ioEngine::delete);
            indexes.forEach((fields, fieldIndex) -> fieldIndex.remove(documentCriteria));
            return paths.size();
        } else {
            return documentsCollection.removeAllThatMatch(documentCriteria);
        }
    }

    @Override
    public List<Document> getAll() {
        return documentsCollection.getAll();
    }

    public static class GenericIndexedDocumentsCollectionBuilder {
        private Path documentsPath;

        private DocumentFactory documentFactory;

        private IndexFactory indexFactory;

        private IOEngine ioEngine;

        public GenericIndexedDocumentsCollectionBuilder setDocumentsPath(Path documentsPath) {
            this.documentsPath = documentsPath;
            return this;
        }

        public GenericIndexedDocumentsCollectionBuilder setDocumentFactory(DocumentFactory documentFactory) {
            this.documentFactory = documentFactory;
            return this;
        }

        public GenericIndexedDocumentsCollectionBuilder setIndexFactory(IndexFactory indexFactory) {
            this.indexFactory = indexFactory;
            return this;
        }

        public GenericIndexedDocumentsCollectionBuilder setIOEngine(IOEngine ioEngine) {
            this.ioEngine = ioEngine;
            return this;
        }

        public BasicIndexedDocumentsCollection build() {
            return new BasicIndexedDocumentsCollection(
                    documentsPath,
                    documentFactory,
                    indexFactory,
                    ioEngine
            );
        }
    }
}

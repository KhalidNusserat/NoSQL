package com.atypon.nosql.database.collection;

import com.atypon.nosql.database.document.Document;
import com.atypon.nosql.database.document.DocumentFactory;
import com.atypon.nosql.database.index.DefaultIndexesManager;
import com.atypon.nosql.database.index.Index;
import com.atypon.nosql.database.index.IndexFactory;
import com.atypon.nosql.database.index.IndexesManager;
import com.atypon.nosql.database.io.IOEngine;
import com.atypon.nosql.database.utils.StopWatch;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
public class BasicIndexedDocumentsCollection implements IndexedDocumentsCollection {
    private final IOEngine ioEngine;

    private final BasicDocumentsCollection documentsCollection;

    private final IndexesManager indexesManager;

    private BasicIndexedDocumentsCollection(
            Path collectionPath,
            DocumentFactory documentFactory,
            IndexFactory indexFactory,
            IOEngine ioEngine) {
        log.info(
                "Initializing an indexed documents collection at {}", 
                collectionPath
        );
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        this.ioEngine = ioEngine;
        Path documentsDirectory = collectionPath.resolve("documents/");
        Path indexesPath = collectionPath.resolve("indexes/");
        documentsCollection = new BasicDocumentsCollection(documentsDirectory, ioEngine);
        indexesManager = new DefaultIndexesManager(indexesPath, indexFactory, ioEngine, documentFactory);
        indexesManager.populateIndexes(documentsDirectory);
        log.info(
                "Finished initializing indexed documents collection at {} in {} second",
                collectionPath,
                stopWatch.end()
        );
    }

    public static GenericIndexedDocumentsCollectionBuilder builder() {
        return new GenericIndexedDocumentsCollectionBuilder();
    }

    @Override
    public void createIndex(Document indexFields) {
        indexesManager.createIndex(indexFields);
    }

    @Override
    public void deleteIndex(Document indexFields) {
        indexesManager.removeIndex(indexFields);
    }

    @Override
    public boolean containsIndex(Document indexFields) {
        return indexesManager.contains(indexFields);
    }

    @Override
    public Collection<Document> getIndexes() {
        return indexesManager.getAllIndexesFields();
    }

    @Override
    public boolean contains(Document documentCriteria) {
        Document criteriaFields = documentCriteria.getFields();
        if (indexesManager.contains(criteriaFields)) {
            Index index = indexesManager.get(criteriaFields);
            return index.contains(documentCriteria);
        } else {
            return documentsCollection.contains(documentCriteria);
        }
    }

    @Override
    public List<Document> getAllThatMatch(Document documentCriteria) {
        Document criteriaFields = documentCriteria.getFields();
        if (indexesManager.contains(criteriaFields)) {
            Index index = indexesManager.get(criteriaFields);
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

    @Override
    public Path addDocument(Document addedDocument) {
        Path addedDocumentPath = documentsCollection.addDocument(addedDocument);
        indexesManager.addDocument(addedDocument, addedDocumentPath);
        return addedDocumentPath;
    }

    @Override
    public Path updateDocument(Document documentCriteria, Document updatedDocument) {
        List<Document> matchingDocuments = documentsCollection.getAllThatMatch(documentCriteria);
        if (matchingDocuments.size() > 1) {
            log.error(
                    "More than one document matched: \"{}\" matched [{}] documents, expected [1]",
                    documentCriteria,
                    matchingDocuments.size()
            );
            throw new MultipleFilesMatchedException(matchingDocuments.size());
        } else if (matchingDocuments.size() == 0) {
            log.error(
                    "No documents matched: \"{}\" matched [0] documents, expected [1]",
                    documentCriteria
            );
            throw new NoSuchDocumentException(documentCriteria);
        } else {
            Document oldDocument = matchingDocuments.get(0);
            Path updatedDocumentPath = documentsCollection.updateDocument(oldDocument, updatedDocument);
            indexesManager.removeDocument(oldDocument);
            indexesManager.addDocument(updatedDocument, updatedDocumentPath);
            return updatedDocumentPath;
        }
    }

    @Override
    public int removeAllThatMatch(Document documentCriteria) {
        Document criteriaFields = documentCriteria.getFields();
        if (indexesManager.contains(criteriaFields)) {
            Collection<Path> paths = indexesManager.get(criteriaFields).get(documentCriteria);
            for (Path path : paths) {
                ioEngine.read(path).ifPresent(indexesManager::removeDocument);
                ioEngine.delete(path);
            }
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
                    ioEngine);
        }
    }
}

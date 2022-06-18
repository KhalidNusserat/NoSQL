package com.atypon.nosql.database.collection;

import com.atypon.nosql.database.document.Document;
import com.atypon.nosql.database.index.Index;
import com.atypon.nosql.database.index.IndexesCollection;
import com.atypon.nosql.database.index.IndexesCollectionFactory;
import com.atypon.nosql.database.io.IOEngine;
import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
public class DefaultIndexedDocumentsCollection implements IndexedDocumentsCollection {
    private final IOEngine ioEngine;

    private final DocumentsCollection documentsCollection;

    private final IndexesCollection indexesCollection;

    public DefaultIndexedDocumentsCollection(
            Path collectionPath,
            IOEngine ioEngine,
            BasicDocumentsCollectionFactory documentsCollectionFactory,
            IndexesCollectionFactory indexesCollectionFactory) {
        log.info(
                "Initializing an indexed documents collection at {}", 
                collectionPath
        );
        Stopwatch stopwatch = Stopwatch.createStarted();
        Path documentsDirectory = collectionPath.resolve("documents/");
        Path indexesPath = collectionPath.resolve("indexes/");
        documentsCollection = documentsCollectionFactory.createCollection(documentsDirectory);
        this.ioEngine = ioEngine;
        this.indexesCollection = indexesCollectionFactory.createIndexesCollection(indexesPath);
        this.indexesCollection.populateIndexes(documentsDirectory);
        log.info(
                "Finished initializing indexed documents collection at {} in {}",
                collectionPath,
                stopwatch.elapsed()
        );
    }

    @Override
    public void createIndex(Document indexFields) {
        indexesCollection.createIndex(indexFields);
    }

    @Override
    public void deleteIndex(Document indexFields) {
        indexesCollection.removeIndex(indexFields);
    }

    @Override
    public boolean containsIndex(Document indexFields) {
        return indexesCollection.contains(indexFields);
    }

    @Override
    public Collection<Document> getIndexes() {
        return indexesCollection.getAllIndexesFields();
    }

    @Override
    public boolean contains(Document documentCriteria) {
        Document criteriaFields = documentCriteria.getFields();
        if (indexesCollection.contains(criteriaFields)) {
            Index index = indexesCollection.get(criteriaFields);
            return index.contains(documentCriteria);
        } else {
            return documentsCollection.contains(documentCriteria);
        }
    }

    @Override
    public List<Document> getAllThatMatch(Document documentCriteria) {
        Document criteriaFields = documentCriteria.getFields();
        if (indexesCollection.contains(criteriaFields)) {
            Index index = indexesCollection.get(criteriaFields);
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
        indexesCollection.addDocument(addedDocument, addedDocumentPath);
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
            indexesCollection.removeDocument(oldDocument);
            indexesCollection.addDocument(updatedDocument, updatedDocumentPath);
            return updatedDocumentPath;
        }
    }

    @Override
    public int removeAllThatMatch(Document documentCriteria) {
        Document criteriaFields = documentCriteria.getFields();
        if (indexesCollection.contains(criteriaFields)) {
            Collection<Path> paths = indexesCollection.get(criteriaFields).get(documentCriteria);
            for (Path path : paths) {
                ioEngine.read(path).ifPresent(indexesCollection::removeDocument);
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
}

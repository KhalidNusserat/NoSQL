package com.atypon.nosql.collection;

import com.atypon.nosql.document.Document;
import com.atypon.nosql.document.DocumentSchema;
import com.atypon.nosql.index.Index;
import com.atypon.nosql.index.IndexesCollection;
import com.atypon.nosql.index.IndexesCollectionFactory;
import com.atypon.nosql.storage.StorageEngine;
import com.atypon.nosql.utils.FileUtils;
import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
public class DefaultIndexedDocumentsCollection implements IndexedDocumentsCollection {
    private final StorageEngine storageEngine;

    private final DocumentsCollection documentsCollection;

    private final IndexesCollection indexes;

    private final Path schemaDirectory;

    private final DocumentSchema documentSchema;

    public DefaultIndexedDocumentsCollection(
            Path collectionPath,
            StorageEngine storageEngine,
            BasicDocumentsCollectionFactory documentsCollectionFactory,
            IndexesCollectionFactory indexesCollectionFactory,
            DocumentSchema documentSchema) {
        log.info(
                "Initializing an indexed documents collection at {}", 
                collectionPath
        );
        Stopwatch stopwatch = Stopwatch.createStarted();
        Path documentsDirectory = collectionPath.resolve("documents/");
        Path indexesDirectory = collectionPath.resolve("indexes/");
        schemaDirectory = collectionPath.resolve("schema/");
        FileUtils.createDirectories(documentsDirectory, indexesDirectory, schemaDirectory);
        documentsCollection = documentsCollectionFactory.createCollection(documentsDirectory);
        this.storageEngine = storageEngine;
        this.indexes = indexesCollectionFactory.createIndexesCollection(indexesDirectory);
        this.indexes.populateIndexes(documentsDirectory);
        this.documentSchema = documentSchema;
        writeSchema();
        log.info(
                "Finished initializing indexed documents collection at {} in {}",
                collectionPath,
                stopwatch.elapsed().getSeconds()
        );
    }

    private void writeSchema() {
        if (FileUtils.countFiles(schemaDirectory, 1) == 0) {
            storageEngine.write(documentSchema.getAsDocument(), schemaDirectory);
        }
    }

    @Override
    public void createIndex(Document indexFields) {
        indexes.createIndex(indexFields);
    }

    @Override
    public void removeIndex(Document indexFields) {
        indexes.removeIndex(indexFields);
    }

    @Override
    public boolean containsIndex(Document indexFields) {
        return indexes.contains(indexFields);
    }

    @Override
    public Collection<Document> getIndexes() {
        return indexes.getAllIndexesFields();
    }

    @Override
    public Document getSchema() {
        return documentSchema.getAsDocument();
    }

    @Override
    public boolean contains(Document documentCriteria) {
        Document criteriaFields = documentCriteria.getFields();
        if (indexes.contains(criteriaFields)) {
            Index index = indexes.get(criteriaFields);
            return index.contains(documentCriteria);
        } else {
            return documentsCollection.contains(documentCriteria);
        }
    }

    @Override
    public List<Document> getAllThatMatch(Document documentCriteria) {
        Document criteriaFields = documentCriteria.getFields();
        if (indexes.contains(criteriaFields)) {
            Index index = indexes.get(criteriaFields);
            return index.get(documentCriteria.getValuesToMatch(index.getFields()))
                    .stream()
                    .map(storageEngine::read)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
        } else {
            return documentsCollection.getAllThatMatch(documentCriteria);
        }
    }

    @Override
    public List<StoredDocument> addDocuments(List<Document> documents) {
        List<StoredDocument> addedDocumentPaths = documentsCollection.addDocuments(documents);
        addedDocumentPaths.forEach(
                storedDocument -> indexes.addDocument(
                        storedDocument.document(),
                        storedDocument.path()
                )
        );
        return addedDocumentPaths;
    }

    @Override
    public List<StoredDocument> updateDocuments(Document documentCriteria, Document updateDocument) {
        Document criteriaFields = documentCriteria.getFields();
        if (indexes.contains(criteriaFields)) {
            Index index = indexes.get(criteriaFields);
            return index.get(criteriaFields)
                    .stream().map(documentPath -> storageEngine.update(updateDocument, documentPath))
                    .toList();
        } else {
            return documentsCollection.updateDocuments(documentCriteria, updateDocument);
        }
    }

    @Override
    public int removeAllThatMatch(Document documentCriteria) {
        Document criteriaFields = documentCriteria.getFields();
        if (indexes.contains(criteriaFields)) {
            Collection<Path> paths = indexes.get(criteriaFields).get(documentCriteria);
            for (Path path : paths) {
                storageEngine.read(path).ifPresent(indexes::removeDocument);
                storageEngine.delete(path);
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

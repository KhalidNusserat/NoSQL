package com.atypon.nosql.collection;

import com.atypon.nosql.document.Document;
import com.atypon.nosql.document.DocumentSchema;
import com.atypon.nosql.index.Index;
import com.atypon.nosql.index.IndexesCollection;
import com.atypon.nosql.index.IndexesCollectionFactory;
import com.atypon.nosql.index.UniqueIndexViolationException;
import com.atypon.nosql.storage.StorageEngine;
import com.atypon.nosql.utils.FileUtils;
import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
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
        this.indexes = indexesCollectionFactory.createIndexesCollection(indexesDirectory, documentsDirectory);
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
            storageEngine.writeDocument(documentSchema.getAsDocument(), schemaDirectory);
        }
    }

    @Override
    public void createIndex(Document indexFields, boolean unique) {
        indexes.createIndex(indexFields, unique);
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
    public Optional<Document> findFirst(Document documentCriteria) {
        return findDocuments(documentCriteria).stream().findFirst();
    }

    @Override
    public List<Document> findDocuments(Document documentCriteria) {
        Document criteriaFields = documentCriteria.getFields();
        if (indexes.contains(criteriaFields)) {
            Index index = indexes.get(criteriaFields);
            return index.get(documentCriteria.getValues(index.getFields()))
                    .stream()
                    .map(storageEngine::readDocument)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
        } else {
            return documentsCollection.findDocuments(documentCriteria);
        }
    }

    @Override
    public List<Stored<Document>> addDocuments(List<Document> documents) {
        if (!documents.stream().allMatch(documentSchema::validate)) {
            throw new DocumentSchemaViolationException();
        }
        documents = documents.stream().map(Document::withId).toList();
        if (!documents.stream().allMatch(indexes::checkUniqueConstraint)) {
            throw new UniqueIndexViolationException();
        }
        List<Stored<Document>> addedDocumentPaths = documentsCollection.addDocuments(documents);
        addedDocumentPaths.forEach(
                storedDocument -> indexes.addDocument(
                        storedDocument.object(),
                        storedDocument.path()
                )
        );
        return addedDocumentPaths;
    }

    @Override
    public List<Stored<Document>> updateDocuments(Document documentCriteria, Document update) {
        Document criteriaFields = documentCriteria.getFields();
        if (indexes.contains(criteriaFields)) {
            Index index = indexes.get(criteriaFields);
            return index.get(criteriaFields).stream()
                    .map(documentPath -> updateDocument(documentPath, update))
                    .filter(Objects::nonNull)
                    .toList();
        } else {
            return documentsCollection.updateDocuments(documentCriteria, update);
        }
    }

    private Stored<Document> updateDocument(Path documentPath, Document update) {
        Optional<Document> optionalDocument = storageEngine.readDocument(documentPath);
        if (optionalDocument.isPresent()) {
            Document document = optionalDocument.get();
            Document updatedDocument = document.overrideFields(update);
            return storageEngine.updateDocument(updatedDocument, documentPath);
        } else {
            return null;
        }
    }

    @Override
    public int removeAllThatMatch(Document documentCriteria) {
        Document criteriaFields = documentCriteria.getFields();
        if (indexes.contains(criteriaFields)) {
            Collection<Path> paths = indexes.get(criteriaFields).get(documentCriteria);
            for (Path path : paths) {
                storageEngine.readDocument(path).ifPresent(indexes::removeDocument);
                storageEngine.deleteFile(path);
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

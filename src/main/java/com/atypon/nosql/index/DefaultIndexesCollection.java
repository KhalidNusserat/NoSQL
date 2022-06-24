package com.atypon.nosql.index;

import com.atypon.nosql.collection.DefaultBasicDocumentsCollection;
import com.atypon.nosql.collection.IndexAlreadyExistsException;
import com.atypon.nosql.collection.NoSuchIndexException;
import com.atypon.nosql.document.Document;
import com.atypon.nosql.document.DocumentFactory;
import com.atypon.nosql.storage.StorageEngine;
import com.atypon.nosql.utils.FileUtils;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultIndexesCollection implements IndexesCollection {
    private final Path indexesDirectory;
    
    private final Map<Document, Index> indexes = new ConcurrentHashMap<>();

    private final Map<Document, Path> indexesPaths = new ConcurrentHashMap<>();

    private final DefaultBasicDocumentsCollection indexesCollection;
    
    private final StorageEngine storageEngine;

    private final IndexDocumentConverter indexDocumentConverter;

    public DefaultIndexesCollection(
            Path indexesDirectory,
            StorageEngine storageEngine,
            DocumentFactory documentFactory,
            IndexDocumentConverter indexDocumentConverter) {
        this.indexesDirectory = indexesDirectory;
        this.storageEngine = storageEngine;
        this.indexDocumentConverter = indexDocumentConverter;
        indexesCollection = new DefaultBasicDocumentsCollection(indexesDirectory, storageEngine);
        FileUtils.createDirectories(indexesDirectory);
        loadIndexes();
        createIdIndex(documentFactory);
    }

    private void loadIndexes() {
        FileUtils.traverseDirectory(indexesDirectory)
                .filter(FileUtils::isJsonFile)
                .forEach(this::loadIndex);

    }

    private void loadIndex(Path indexPath) {
        Optional<Document> indexDocument = storageEngine.read(indexPath);
        if (indexDocument.isPresent()) {
            Index index = indexDocumentConverter.toIndex(indexDocument.get());
            indexes.put(index.getFields(), index);
            indexesPaths.put(index.getFields(), indexPath);
        }
    }

    private void createIdIndex(DocumentFactory documentFactory) {
        Document idIndexFields = documentFactory.createFromString("{_id: null}");
        if (!indexes.containsKey(idIndexFields)) {
            createIndex(idIndexFields, true);
        }
    }

    @Override
    public void createIndex(Document indexFields, boolean unqiue) {
        if (indexes.containsKey(indexFields)) {
            throw new IndexAlreadyExistsException(indexFields);
        }
        DefaultIndex index = new DefaultIndex(indexFields, unqiue);
        indexes.put(indexFields, index);
        indexesCollection.addDocuments(List.of(indexDocumentConverter.toDocument(index)));
    }

    @Override
    public void removeIndex(Document indexFields) {
        if (!indexes.containsKey(indexFields)) {
            throw new NoSuchIndexException(indexFields);
        }
        indexes.remove(indexFields);
        indexesCollection.removeAllThatMatch(indexFields);
    }

    @Override
    public Index get(Document indexFields) {
        return indexes.get(indexFields);
    }

    @Override
    public List<Document> getAllIndexesFields() {
        return indexes.keySet().stream().toList();
    }

    @Override
    public void addDocument(Document document, Path documentPath) {
        for (Index index : indexes.values()) {
            index.add(document, documentPath);
            updateStoredIndex(index);
        }
    }

    private void updateStoredIndex(Index index) {
        Document updatedIndexDocument = indexDocumentConverter.toDocument(index);
        Document fields = index.getFields();
        storageEngine.update(updatedIndexDocument, indexesPaths.get(fields));
    }

    @Override
    public void removeDocument(Document document) {
        for (Index index : indexes.values()) {
            index.remove(document);
            updateStoredIndex(index);
        }
    }

    @Override
    public boolean contains(Document indexFields) {
        return indexes.containsKey(indexFields);
    }

    @Override
    public void populateIndexes(Path documentsDirectory) {
        FileUtils.traverseDirectory(documentsDirectory)
                .filter(FileUtils::isJsonFile)
                .forEach(this::addDocumentToIndexes);
    }

    private void addDocumentToIndexes(Path documentPath) {
        storageEngine.read(documentPath).ifPresent(document -> addDocument(document, documentPath));
    }
}

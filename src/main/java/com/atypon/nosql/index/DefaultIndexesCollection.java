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

    private final DefaultBasicDocumentsCollection indexesCollection;
    
    private final StorageEngine storageEngine;

    public DefaultIndexesCollection(
            Path indexesDirectory,
            StorageEngine storageEngine,
            DocumentFactory documentFactory) {
        this.indexesDirectory = indexesDirectory;
        this.storageEngine = storageEngine;
        indexesCollection = new DefaultBasicDocumentsCollection(indexesDirectory, storageEngine);
        FileUtils.createDirectories(indexesDirectory);
        loadIndexes();
        createIdIndex(documentFactory);
    }

    private void loadIndexes() {
        FileUtils.traverseDirectory(indexesDirectory)
                .filter(FileUtils::isJsonFile)
                .map(storageEngine::read)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(indexFields -> indexes.put(indexFields, new DefaultIndex(indexFields)));
    }

    private void createIdIndex(DocumentFactory documentFactory) {
        Document idIndexFields = documentFactory.createFromString("{_id: null}");
        Index idIndex = new DefaultIndex(idIndexFields);
        if (!indexes.containsKey(idIndexFields)) {
            indexes.put(idIndexFields, idIndex);
            indexesCollection.addDocuments(List.of(idIndexFields));
        }
    }

    private void addDocument(Path documentPath) {
        storageEngine.read(documentPath).ifPresent(document -> addDocument(document, documentPath));
    }

    @Override
    public void createIndex(Document indexFields) {
        if (indexes.containsKey(indexFields)) {
            throw new IndexAlreadyExistsException(indexFields);
        }
        indexes.put(indexFields, new DefaultIndex(indexFields));
        indexesCollection.addDocuments(List.of(indexFields));
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
        }
    }

    @Override
    public void removeDocument(Document document) {
        for (Index index : indexes.values()) {
            index.remove(document);
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
                .forEach(this::addDocument);
    }
}
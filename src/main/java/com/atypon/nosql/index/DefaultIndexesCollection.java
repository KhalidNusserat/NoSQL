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

    private final StorageEngine storageEngine;

    private final DefaultBasicDocumentsCollection indexesCollection;

    private final DocumentFactory documentFactory;

    public DefaultIndexesCollection(
            Path indexesDirectory,
            StorageEngine storageEngine,
            DocumentFactory documentFactory) {
        this.indexesDirectory = indexesDirectory;
        this.storageEngine = storageEngine;
        this.documentFactory = documentFactory;
        indexesCollection = new DefaultBasicDocumentsCollection(indexesDirectory, storageEngine);
        FileUtils.createDirectories(indexesDirectory);
        loadIndexes();
        createIdIndex();
    }

    private void loadIndexes() {
        FileUtils.traverseDirectory(indexesDirectory)
                .filter(FileUtils::isJsonFile)
                .forEach(this::loadIndex);
    }

    @SuppressWarnings("unchecked")
    private void loadIndex(Path indexPath) {
        Optional<Document> optionalIndexProperties = storageEngine.readDocument(indexPath);
        if (optionalIndexProperties.isPresent()) {
            Map<String, Object> indexProperties = optionalIndexProperties.get().getAsMap();
            boolean unique = (boolean) indexProperties.get("unique");
            Document indexFields = documentFactory.createFromMap((Map<String, Object>) indexProperties.get("fields"));
            indexes.put(indexFields, new DefaultIndex(indexFields, unique));
        }
    }

    private void createIdIndex() {
        Document idIndexFields = documentFactory.createFromString("{_id: null}");
        if (!indexes.containsKey(idIndexFields)) {
            createIndex(idIndexFields, true);
        }
    }

    @Override
    public void createIndex(Document indexFields, boolean unique) {
        if (indexes.containsKey(indexFields)) {
            throw new IndexAlreadyExistsException(indexFields);
        }
        DefaultIndex index = new DefaultIndex(indexFields, unique);
        indexes.put(indexFields, index);
        Document indexProperties = documentFactory.createFromMap(
                Map.of("unique", unique,
                        "fields", indexFields.getAsMap())
        );
        indexesCollection.addDocuments(List.of(indexProperties));
    }

    @Override
    public void removeIndex(Document indexFields) {
        if (!indexes.containsKey(indexFields)) {
            throw new NoSuchIndexException(indexFields);
        }
        Document criteria = documentFactory.createFromMap(Map.of("fields", indexFields.getAsMap()));
        indexesCollection.removeAllThatMatch(criteria);
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
                .forEach(this::addDocumentToIndexes);
    }

    private void addDocumentToIndexes(Path documentPath) {
        storageEngine.readDocument(documentPath).ifPresent(document -> addDocument(document, documentPath));
    }
}

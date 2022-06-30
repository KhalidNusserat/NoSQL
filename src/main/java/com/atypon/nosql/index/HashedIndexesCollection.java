package com.atypon.nosql.index;

import com.atypon.nosql.collection.DefaultBasicDocumentsCollection;
import com.atypon.nosql.collection.IndexAlreadyExistsException;
import com.atypon.nosql.collection.NoSuchIndexException;
import com.atypon.nosql.document.Document;
import com.atypon.nosql.storage.StorageEngine;
import com.atypon.nosql.utils.FileUtils;
import lombok.ToString;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@ToString
public class HashedIndexesCollection implements IndexesCollection {

    private final Path indexesDirectory;

    private final Path documentsDirectory;

    private final Map<Document, Index> indexes = new ConcurrentHashMap<>();

    private final StorageEngine storageEngine;

    private final DefaultBasicDocumentsCollection indexesCollection;

    public HashedIndexesCollection(
            Path indexesDirectory,
            Path documentsDirectory,
            StorageEngine storageEngine) {
        this.indexesDirectory = indexesDirectory;
        this.documentsDirectory = documentsDirectory;
        this.storageEngine = storageEngine;
        indexesCollection = new DefaultBasicDocumentsCollection(indexesDirectory, storageEngine);
        FileUtils.createDirectories(indexesDirectory);
        loadIndexes();
        createIdIndex();
    }

    private void loadIndexes() {
        FileUtils.traverseDirectory(indexesDirectory)
                .filter(FileUtils::isJsonFile)
                .forEach(this::loadIndex);
        populateIndexes();
    }

    @SuppressWarnings("unchecked")
    private void loadIndex(Path indexPath) {
        Optional<Document> optionalIndexProperties = storageEngine.readDocument(indexPath);
        if (optionalIndexProperties.isPresent()) {
            Map<String, Object> indexProperties = optionalIndexProperties.get().toMap();
            boolean unique = (boolean) indexProperties.get("unique");
            Document indexFields = Document.fromMap((Map<String, Object>) indexProperties.get("fields"));
            indexes.put(indexFields, new HashedIndex(indexFields, unique));
        }
    }

    private void createIdIndex() {
        Document idIndexFields = Document.of("_id", null);
        if (!indexes.containsKey(idIndexFields)) {
            createIndex(idIndexFields, true);
        }
    }

    @Override
    public void createIndex(Document indexFields, boolean unique) {
        if (indexes.containsKey(indexFields)) {
            throw new IndexAlreadyExistsException(indexFields);
        }
        HashedIndex index = new HashedIndex(indexFields, unique);
        populateIndex(index, documentsDirectory);
        indexes.put(indexFields, index);
        Document indexProperties = Document.of(
                "unique", unique,
                "fields", indexFields.toMap()
        );
        indexesCollection.addAll(List.of(indexProperties));
    }

    private void populateIndex(Index index, Path documentsDirectory) {
        FileUtils.traverseDirectory(documentsDirectory)
                .filter(FileUtils::isJsonFile)
                .forEach(documentPath -> {
                    Optional<Document> optionalDocument = storageEngine.readDocument(documentPath);
                    optionalDocument.ifPresent(document -> index.add(document, documentPath));
                });
    }

    @Override
    public void removeIndex(Document indexFields) {
        if (!indexes.containsKey(indexFields)) {
            throw new NoSuchIndexException(indexFields);
        }
        Document criteria = Document.of("fields", indexFields.toMap());
        indexesCollection.removeAll(criteria);
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
    public void populateIndexes() {
        FileUtils.traverseDirectory(documentsDirectory)
                .filter(FileUtils::isJsonFile)
                .forEach(this::addDocumentToIndexes);
    }

    private void addDocumentToIndexes(Path documentPath) {
        storageEngine.readDocument(documentPath).ifPresent(document -> addDocument(document, documentPath));
    }

    @Override
    public boolean checkUniqueConstraint(Document document) {
        return indexes.values().stream().allMatch(index -> index.checkUniqueConstraint(document));
    }
}

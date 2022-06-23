package com.atypon.nosql;

import com.atypon.nosql.collection.IndexedDocumentsCollection;
import com.atypon.nosql.collection.IndexedDocumentsCollectionFactory;
import com.atypon.nosql.document.Document;
import com.atypon.nosql.utils.FileUtils;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DefaultDatabase implements Database {
    private final Map<String, IndexedDocumentsCollection> documentsCollections = new ConcurrentHashMap<>();

    private final Path databaseDirectory;

    private final ExecutorService directoriesDeletingService = Executors.newCachedThreadPool();

    private final IndexedDocumentsCollectionFactory documentsCollectionFactory;

    public DefaultDatabase(Path databaseDirectory, IndexedDocumentsCollectionFactory documentsCollectionFactory) {
        this.databaseDirectory = databaseDirectory;
        this.documentsCollectionFactory = documentsCollectionFactory;
        FileUtils.createDirectories(databaseDirectory);
        FileUtils.traverseDirectory(databaseDirectory)
                .filter(path -> !path.equals(databaseDirectory))
                .forEach(this::loadCollection);
    }

    private void loadCollection(Path collectionDirectory) {
        IndexedDocumentsCollection documentsCollection =
                documentsCollectionFactory.createCollection(collectionDirectory);
        String collectionName = collectionDirectory.getFileName().toString();
        documentsCollections.put(collectionName, documentsCollection);
    }

    private void checkCollectionExists(String collectionName) {
        if (!documentsCollections.containsKey(collectionName)) {
            throw new CollectionNotFoundException(collectionName);
        }
    }

    @Override
    public void createCollection(String collectionName, Document schemaDocument) {
        if (documentsCollections.containsKey(collectionName)) {
            throw new CollectionAlreadyExists(collectionName);
        }
        Path collectionDirectory = databaseDirectory.resolve(collectionName + "/");
        IndexedDocumentsCollection documentsCollection = documentsCollectionFactory.createCollection(
                collectionDirectory,
                schemaDocument
        );
        documentsCollections.put(collectionName, documentsCollection);
    }

    @Override
    public void removeCollection(String collectionName) {
        checkCollectionExists(collectionName);
        Path collectionDirectory = databaseDirectory.resolve(collectionName + "/");
        documentsCollections.remove(collectionName);
        directoriesDeletingService.submit(() -> FileUtils.deleteDirectory(collectionDirectory));
    }

    @Override
    public IndexedDocumentsCollection get(String collectionName) {
        return documentsCollections.get(collectionName);
    }

    @Override
    public Collection<String> getCollectionsNames() {
        return documentsCollections.keySet();
    }

    @Override
    public void deleteDatabase() {
        for (String collection : documentsCollections.keySet()) {
            removeCollection(collection);
        }
    }
}

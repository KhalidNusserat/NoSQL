package com.atypon.nosql;

import com.atypon.nosql.collection.IndexedDocumentsCollection;
import com.atypon.nosql.database.Database;
import com.atypon.nosql.database.DatabaseFactory;
import com.atypon.nosql.document.Document;
import com.atypon.nosql.document.DocumentFactory;
import com.atypon.nosql.utils.FileUtils;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DefaultDatabasesManager implements DatabasesManager {
    private final Path databasesDirectory;

    private final Map<String, Database> databases = new ConcurrentHashMap<>();

    private final DatabaseFactory databaseFactory;

    private final DocumentFactory documentFactory;

    public DefaultDatabasesManager(
            Path databasesDirectory,
            DatabaseFactory databaseFactory,
            DocumentFactory documentFactory) {
        this.databasesDirectory = databasesDirectory;
        this.databaseFactory = databaseFactory;
        this.documentFactory = documentFactory;
        FileUtils.createDirectories(databasesDirectory);
        loadDatabases();
    }

    private void loadDatabases() {
        FileUtils.traverseDirectory(databasesDirectory)
                .filter(path -> Files.isDirectory(path) && !path.equals(databasesDirectory))
                .forEach(this::loadDatabase);
    }

    private void loadDatabase(Path databaseDirectory) {
        String databaseName = databaseDirectory.getFileName().toString();
        databases.put(databaseName, databaseFactory.create(databaseDirectory));
    }

    @Override
    public void createDatabase(String databaseName) {
        Path databaseDirectory = databasesDirectory.resolve(databaseName + "/");
        databases.put(databaseName, databaseFactory.create(databaseDirectory));
    }

    @Override
    public void removeDatabase(String databaseName) {
        checkDatabaseExists(databaseName);
        databases.get(databaseName).deleteDatabase();
        databases.remove(databaseName);
    }

    private void checkDatabaseExists(String database) {
        if (!databases.containsKey(database)) {
            throw new NoSuchDatabaseException(database);
        }
    }

    @Override
    public Collection<String> getDatabasesNames() {
        return databases.keySet();
    }

    @Override
    public void createCollection(
            String databaseName,
            String collectionName,
            Map<String, Object> documentsSchema) {
        checkDatabaseExists(databaseName);
        Database database = databases.get(databaseName);
        database.createCollection(collectionName, documentFactory.createFromMap(documentsSchema));
    }

    @Override
    public void removeCollection(String databaseName, String collectionName) {
        checkDatabaseExists(databaseName);
        Database database = databases.get(databaseName);
        database.removeCollection(collectionName);
    }

    @Override
    public Map<String, Object> getDocumentsCollectionSchema(String databaseName, String collectionName) {
        checkDatabaseExists(databaseName);
        Database database = databases.get(databaseName);
        IndexedDocumentsCollection documentsCollection = database.get(collectionName);
        return documentsCollection.getSchema().getAsMap();
    }

    @Override
    public Collection<String> getCollectionsNames(String databaseName) {
        checkDatabaseExists(databaseName);
        Database database = databases.get(databaseName);
        return database.getCollectionsNames();
    }

    @Override
    public void addDocuments(
            String databaseName,
            String collectionName,
            List<Map<String, Object>> documentsMaps) {
        IndexedDocumentsCollection documentsCollection = getDocumentsCollection(databaseName, collectionName);
        List<Document> documents = documentsMaps.stream().map(documentFactory::createFromMap).toList();
        documentsCollection.addDocuments(documents);
    }

    private IndexedDocumentsCollection getDocumentsCollection(String databaseName, String collectionName) {
        checkDatabaseExists(databaseName);
        Database database = databases.get(databaseName);
        return database.get(collectionName);
    }

    @Override
    public int removeDocuments(
            String databaseName,
            String collectionName,
            Map<String, Object> documentCriteriaMap) {
        IndexedDocumentsCollection documentsCollection = getDocumentsCollection(databaseName, collectionName);
        Document documentCriteria = documentFactory.createFromMap(documentCriteriaMap);
        return documentsCollection.removeAllThatMatch(documentCriteria);
    }

    @Override
    public List<Map<String, Object>> getDocuments(
            String databaseName,
            String collectionName,
            Map<String, Object> documentCriteriaMap) {
        IndexedDocumentsCollection documentsCollection = getDocumentsCollection(databaseName, collectionName);
        Document documentCriteria = documentFactory.createFromMap(documentCriteriaMap);
        return documentsToMaps(documentsCollection.getAllThatMatch(documentCriteria));
    }

    private List<Map<String, Object>> documentsToMaps(Collection<Document> documents) {
        return documents.stream().map(Document::getAsMap).toList();
    }

    public int updateDocuments(
            String databaseName,
            String collectionName,
            Map<String, Object> criteriaMap,
            Map<String, Object> updatedDocumentMap) {
        IndexedDocumentsCollection documentsCollection = getDocumentsCollection(databaseName, collectionName);
        Document documentCriteria = documentFactory.createFromMap(criteriaMap);
        Document updatedDocument = documentFactory.createFromMap(updatedDocumentMap);
        return documentsCollection.updateDocuments(documentCriteria, updatedDocument).size();
    }

    @Override
    public void createIndex(String databaseName, String collectionName, Map<String, Object> indexMap, boolean unique) {
        IndexedDocumentsCollection documentsCollection = getDocumentsCollection(databaseName, collectionName);
        Document indexDocument = documentFactory.createFromMap(indexMap);
        documentsCollection.createIndex(indexDocument, unique);
    }

    @Override
    public void removeIndex(String databaseName, String collectionName, Map<String, Object> indexMap) {
        IndexedDocumentsCollection documentsCollection = getDocumentsCollection(databaseName, collectionName);
        Document indexDocument = documentFactory.createFromMap(indexMap);
        documentsCollection.removeIndex(indexDocument);
    }

    @Override
    public Collection<Map<String, Object>> getCollectionIndexes(String databaseName, String collectionName) {
        IndexedDocumentsCollection documentsCollection = getDocumentsCollection(databaseName, collectionName);
        return documentsToMaps(documentsCollection.getIndexes());
    }
}

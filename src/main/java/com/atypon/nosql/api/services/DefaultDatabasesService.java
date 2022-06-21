package com.atypon.nosql.api.services;

import com.atypon.nosql.api.controllers.NoSuchDatabaseException;
import com.atypon.nosql.database.Database;
import com.atypon.nosql.database.DatabaseFactory;
import com.atypon.nosql.database.collection.IndexedDocumentsCollection;
import com.atypon.nosql.database.document.Document;
import com.atypon.nosql.database.document.DocumentFactory;
import com.atypon.nosql.database.utils.FileUtils;
import com.atypon.nosql.synchronisation.SynchronisationService;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DefaultDatabasesService implements DatabasesService {
    private final Path databasesDirectory;

    private final Map<String, Database> databases = new ConcurrentHashMap<>();

    private final DatabaseFactory databaseFactory;

    private final DocumentFactory documentFactory;

    private final DocumentTranslator documentTranslator;

    private final SynchronisationService synchronisationService;

    public DefaultDatabasesService(
            Path databasesDirectory,
            DatabaseFactory databaseFactory,
            DocumentFactory documentFactory,
            DocumentTranslator documentTranslator,
            SynchronisationService synchronisationService) {
        this.databasesDirectory = databasesDirectory;
        this.databaseFactory = databaseFactory;
        this.documentFactory = documentFactory;
        this.documentTranslator = documentTranslator;
        this.synchronisationService = synchronisationService;
        FileUtils.createDirectories(databasesDirectory);
        loadDatabases();
    }

    public void loadDatabases() {
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
        synchronisationService
                .newInstance()
                .method(HttpMethod.POST)
                .url("/databases/{database}")
                .parameters(databaseName)
                .synchronise();
    }

    private void checkDatabaseExists(String database) {
        if (!databases.containsKey(database)) {
            throw new NoSuchDatabaseException(database);
        }
    }

    @Override
    public void removeDatabase(String databaseName) {
        checkDatabaseExists(databaseName);
        databases.get(databaseName).deleteDatabase();
        databases.remove(databaseName);
        synchronisationService
                .newInstance()
                .method(HttpMethod.DELETE)
                .url("/databases/{database}")
                .parameters(databaseName)
                .synchronise();
    }

    @Override
    public Collection<String> getDatabasesNames() {
        return databases.keySet();
    }

    @Override
    public void createDocumentsCollection(
            String databaseName,
            String collectionName,
            Map<String, Object> documentsSchema) {
        checkDatabaseExists(databaseName);
        Database database = databases.get(databaseName);
        database.createCollection(collectionName, documentFactory.createFromMap(documentsSchema));
        synchronisationService
                .newInstance()
                .method(HttpMethod.POST)
                .requestBody(documentsSchema)
                .url("/databases/{database}/collections/{collection}")
                .parameters(databaseName, collectionName)
                .synchronise();
    }

    @Override
    public void removeDocumentsCollection(String databaseName, String collectionName) {
        checkDatabaseExists(databaseName);
        Database database = databases.get(databaseName);
        database.removeCollection(collectionName);
        synchronisationService
                .newInstance()
                .method(HttpMethod.DELETE)
                .url("/databases/{database}/collections/{collection}")
                .parameters(databaseName, collectionName)
                .synchronise();
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
    public void addDocument(String databaseName, String collectionName, Map<String, Object> documentMap) {
        IndexedDocumentsCollection documentsCollection = getDocumentsCollection(databaseName, collectionName);
        Document document = documentTranslator.translate(documentMap);
        documentsCollection.addDocument(document);
        synchronisationService
                .newInstance()
                .method(HttpMethod.POST)
                .requestBody(document.getAsMap())
                .url("/databases/{database}/collections/{collection}/documents")
                .parameters(databaseName, collectionName)
                .synchronise();
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
        int removedCount = documentsCollection.removeAllThatMatch(documentCriteria);
        synchronisationService
                .newInstance()
                .method(HttpMethod.DELETE)
                .requestBody(documentCriteriaMap)
                .url("/databases/{database}/collections/{collection}/documents")
                .parameters(databaseName, collectionName)
                .synchronise();
        return removedCount;
    }

    @Override
    public Collection<Map<String, Object>> getDocuments(
            String databaseName,
            String collectionName,
            Map<String, Object> documentCriteriaMap) {
        IndexedDocumentsCollection documentsCollection = getDocumentsCollection(databaseName, collectionName);
        Document documentCriteria = documentFactory.createFromMap(documentCriteriaMap);
        return documentsToMaps(documentsCollection.getAllThatMatch(documentCriteria));
    }

    private Collection<Map<String, Object>> documentsToMaps(Collection<Document> documents) {
        return documents.stream().map(Document::getAsMap).toList();
    }

    public void updateDocument(
            String databaseName,
            String collectionName,
            String documentId,
            Map<String, Object> updatedDocumentMap) {
        IndexedDocumentsCollection documentsCollection = getDocumentsCollection(databaseName, collectionName);
        Document documentCriteria = documentFactory.createFromMap(Map.of("_id", documentId));
        Document updatedDocument = documentTranslator.translate(updatedDocumentMap);
        documentsCollection.updateDocument(documentCriteria, updatedDocument);
        synchronisationService
                .newInstance()
                .method(HttpMethod.PUT)
                .requestBody(updatedDocument.getAsMap())
                .url("/databases/{database}/collections/{collection}/documents/{documentId}")
                .parameters(databaseName, collectionName, documentId)
                .synchronise();
    }

    @Override
    public void createIndex(String databaseName, String collectionName, Map<String, Object> indexMap) {
        IndexedDocumentsCollection documentsCollection = getDocumentsCollection(databaseName, collectionName);
        Document indexDocument = documentFactory.createFromMap(indexMap);
        documentsCollection.createIndex(indexDocument);
        synchronisationService
                .newInstance()
                .method(HttpMethod.POST)
                .requestBody(indexMap)
                .url("/databases/{database}/collections/{collection}/indexes")
                .parameters(databaseName, collectionName)
                .synchronise();
    }

    @Override
    public void removeIndex(String databaseName, String collectionName, Map<String, Object> indexMap) {
        IndexedDocumentsCollection documentsCollection = getDocumentsCollection(databaseName, collectionName);
        Document indexDocument = documentFactory.createFromMap(indexMap);
        documentsCollection.removeIndex(indexDocument);
        synchronisationService
                .newInstance()
                .method(HttpMethod.DELETE)
                .requestBody(indexMap)
                .url("/databases/{database}/collections/{collection}/indexes")
                .parameters(databaseName, collectionName)
                .synchronise();
    }

    @Override
    public Collection<Map<String, Object>> getCollectionIndexes(String databaseName, String collectionName) {
        IndexedDocumentsCollection documentsCollection = getDocumentsCollection(databaseName, collectionName);
        return documentsToMaps(documentsCollection.getIndexes());
    }
}

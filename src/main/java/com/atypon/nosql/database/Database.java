package com.atypon.nosql.database;

import java.util.Collection;
import java.util.Map;

public interface Database {
    void createCollection(String collectionName, String schemaString);

    void deleteCollection(String collectionName);

    void addDocument(String collectionName, String documentString);

    void updateDocument(String collectionName, String documentID, String updatedDocumentString);

    Collection<Map<String, Object>> readDocuments(String collectionName, String matchDocumentString);

    void deleteDocuments(String collectionName, String matchDocumentString);

    Collection<Map<String, Object>> getCollectionIndexes(String collectionName);

    void createIndex(String collectionName, String indexDocumentString);

    void deleteIndex(String collectionName, String indexDocumentString);

    Collection<String> getCollectionsNames();

    Map<String, Object> getCollectionSchema(String collectionName);

    void deleteDatabase();
}

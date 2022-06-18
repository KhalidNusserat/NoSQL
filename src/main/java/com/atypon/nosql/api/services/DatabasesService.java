package com.atypon.nosql.api.services;

import java.util.Collection;
import java.util.Map;

public interface DatabasesService {
    void createDatabase(String databaseName);

    void removeDatabase(String databaseName);

    Collection<String> getDatabasesNames();

    void createDocumentsCollection(
            String databaseName,
            String collectionName,
            Map<String, Object> documentsSchemaMap
    );

    void removeDocumentsCollection(String databaseName, String collectionName);

    Map<String, Object> getDocumentsCollectionSchema(String databaseName, String collectionName);

    Collection<String> getCollectionsNames(String databaseName);

    void addDocument(
            String databaseName,
            String collectionName,
            Map<String, Object> documentMap
    );

    int removeDocuments(
            String databaseName,
            String collectionName,
            Map<String, Object> documentCriteriaMap
    );

    Collection<Map<String, Object>> getDocuments(
            String databaseName,
            String collectionName,
            Map<String, Object> documentCriteriaMap
    );

    void updateDocument(
            String databaseName,
            String collectionName,
            String documentId,
            Map<String, Object> updatedDocumentMap
    );

    void createIndex(
            String databaseName,
            String collectionName,
            Map<String, Object> indexMap);

    void removeIndex(
            String databaseName,
            String collectionName,
            Map<String, Object> indexMap);

    Collection<Map<String, Object>> getCollectionIndexes(String databaseName, String collectionName);
}

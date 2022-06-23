package com.atypon.nosql;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface DatabasesManager {
    void createDatabase(String databaseName);

    void removeDatabase(String databaseName);

    Collection<String> getDatabasesNames();

    void createCollection(
            String databaseName,
            String collectionName,
            Map<String, Object> documentsSchemaMap
    );

    void removeCollection(String databaseName, String collectionName);

    Map<String, Object> getDocumentsCollectionSchema(String databaseName, String collectionName);

    Collection<String> getCollectionsNames(String databaseName);

    void addDocuments(
            String databaseName,
            String collectionName,
            List<Map<String, Object>> documentMap
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

    int updateDocuments(
            String databaseName,
            String collectionName,
            Map<String, Object> criteriaMap,
            Map<String, Object> updatedDocumentMap
    );

    void createIndex(
            String databaseName,
            String collectionName,
            Map<String, Object> indexMap
    );

    void removeIndex(
            String databaseName,
            String collectionName,
            Map<String, Object> indexMap
    );

    Collection<Map<String, Object>> getCollectionIndexes(String databaseName, String collectionName);
}

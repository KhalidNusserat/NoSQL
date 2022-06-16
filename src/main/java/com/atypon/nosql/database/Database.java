package com.atypon.nosql.database;

import com.atypon.nosql.database.collection.IndexedDocumentsCollection;
import com.atypon.nosql.database.document.Document;

import java.util.Collection;
import java.util.Map;

public interface Database<T extends Document> {
    void createCollection(String collectionName, String schemaString);

    void removeCollection(String collectionName);

    IndexedDocumentsCollection<T> get(String collectionName);

    Collection<String> getCollectionsNames();

    Map<String, Object> getCollectionSchema(String collectionName);

    void deleteDatabase();
}

package com.atypon.nosql.database;

import com.atypon.nosql.database.collection.IndexedDocumentsCollection;
import com.atypon.nosql.database.document.Document;

import java.util.Collection;
import java.util.Map;

public interface Database {
    void createCollection(String collectionName, Document schema);

    void removeCollection(String collectionName);

    IndexedDocumentsCollection get(String collectionName);

    Collection<String> getCollectionsNames();

    void deleteDatabase();
}

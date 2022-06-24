package com.atypon.nosql.database;

import com.atypon.nosql.collection.IndexedDocumentsCollection;
import com.atypon.nosql.document.Document;

import java.util.Collection;

public interface Database {
    void createCollection(String collectionName, Document schema);

    void removeCollection(String collectionName);

    IndexedDocumentsCollection get(String collectionName);

    Collection<String> getCollectionsNames();

    void deleteDatabase();
}

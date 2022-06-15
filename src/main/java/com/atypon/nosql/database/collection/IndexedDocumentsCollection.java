package com.atypon.nosql.database.collection;

import com.atypon.nosql.database.document.Document;

import java.util.Collection;

public interface IndexedDocumentsCollection<T extends Document> extends DocumentsCollection<T> {
    void createIndex(T matchDocument);

    void deleteIndex(T matchDocument);

    Collection<T> getIndexes();
}

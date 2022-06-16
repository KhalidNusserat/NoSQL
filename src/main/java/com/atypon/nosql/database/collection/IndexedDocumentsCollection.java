package com.atypon.nosql.database.collection;

import com.atypon.nosql.database.document.Document;

import java.util.Collection;

public interface IndexedDocumentsCollection extends DocumentsCollection {
    void createIndex(Document documentCriteria);

    void deleteIndex(Document documentCriteria);

    Collection<Document> getIndexes();
}

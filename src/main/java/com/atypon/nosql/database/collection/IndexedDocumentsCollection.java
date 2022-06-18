package com.atypon.nosql.database.collection;

import com.atypon.nosql.database.document.Document;

import java.util.Collection;

public interface IndexedDocumentsCollection extends DocumentsCollection {
    void createIndex(Document indexDocument);

    void removeIndex(Document indexDocument);

    boolean containsIndex(Document indexDocument);

    Collection<Document> getIndexes();

    Document getSchema();
}

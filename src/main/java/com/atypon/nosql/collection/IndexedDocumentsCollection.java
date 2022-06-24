package com.atypon.nosql.collection;

import com.atypon.nosql.document.Document;

import java.util.Collection;

public interface IndexedDocumentsCollection extends DocumentsCollection {
    void createIndex(Document indexDocument, boolean unique);

    void removeIndex(Document indexDocument);

    boolean containsIndex(Document indexDocument);

    Collection<Document> getIndexes();

    Document getSchema();
}

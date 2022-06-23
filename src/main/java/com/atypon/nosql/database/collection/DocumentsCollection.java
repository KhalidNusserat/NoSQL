package com.atypon.nosql.database.collection;

import com.atypon.nosql.database.document.Document;

import java.util.List;

public interface DocumentsCollection {
    boolean contains(Document matchDocument);

    List<Document> getAllThatMatch(Document documentCriteria);

    List<StoredDocument> addDocuments(List<Document> documents);

    List<StoredDocument> updateDocuments(Document documentCriteria, Document updatedDocument);

    int removeAllThatMatch(Document documentCriteria);

    List<Document> getAll();
}

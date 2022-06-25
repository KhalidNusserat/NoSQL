package com.atypon.nosql.collection;

import com.atypon.nosql.document.Document;

import java.util.List;

public interface DocumentsCollection {
    boolean contains(Document matchDocument);

    List<Document> getAllThatMatch(Document documentCriteria);

    List<Stored<Document>> addDocuments(List<Document> documents);

    List<Stored<Document>> updateDocuments(Document documentCriteria, Document updatedDocument);

    int removeAllThatMatch(Document documentCriteria);

    List<Document> getAll();
}

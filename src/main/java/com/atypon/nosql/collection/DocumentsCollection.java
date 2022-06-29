package com.atypon.nosql.collection;

import com.atypon.nosql.document.Document;

import java.util.List;
import java.util.Optional;

public interface DocumentsCollection {
    boolean contains(Document matchDocument);

    Optional<Document> findFirst(Document criteria);

    List<Document> findAll(Document criteria);

    List<Stored<Document>> addAll(List<Document> documents);

    List<Stored<Document>> updateAll(Document criteria, Document update);

    int removeAll(Document documentCriteria);

    List<Document> getAll();
}

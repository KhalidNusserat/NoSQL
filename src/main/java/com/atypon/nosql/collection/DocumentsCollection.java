package com.atypon.nosql.collection;

import com.atypon.nosql.document.Document;

import javax.print.Doc;
import java.util.List;
import java.util.Optional;

public interface DocumentsCollection {
    boolean contains(Document matchDocument);

    Optional<Document> findFirst(Document documentCriteria);

    List<Document> findDocuments(Document documentCriteria);

    List<Stored<Document>> addDocuments(List<Document> documents);

    List<Stored<Document>> updateDocuments(Document documentCriteria, Document updatedDocument);

    int removeAllThatMatch(Document documentCriteria);

    List<Document> getAll();
}

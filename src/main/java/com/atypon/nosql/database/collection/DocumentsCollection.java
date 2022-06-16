package com.atypon.nosql.database.collection;

import com.atypon.nosql.database.document.Document;

import java.nio.file.Path;
import java.util.List;

public interface DocumentsCollection {
    boolean contains(Document matchDocument);

    List<Document> getAllThatMatch(Document matchDocument);

    Path addDocument(Document document);

    Path updateDocument(Document documentCriteria, Document updatedDocument);

    int removeAllThatMatch(Document matchDocument);

    List<Document> getAll();
}

package com.atypon.nosql.database.collection;

import com.atypon.nosql.database.document.Document;

import java.nio.file.Path;
import java.util.Collection;

public interface DocumentsCollection {
    boolean contains(Document matchDocument);

    Collection<Document> getAllThatMatch(Document matchDocument);

    Path addDocument(Document document);

    Path updateDocument(Document documentCriteria, Document updatedDocument);

    int removeAllThatMatch(Document matchDocument);

    Collection<Document> getAll();
}

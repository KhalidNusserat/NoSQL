package com.atypon.nosql.database.collection;

import com.atypon.nosql.database.document.Document;

import java.nio.file.Path;
import java.util.Collection;

public interface DocumentsCollection<T extends Document> {
    boolean contains(T matchDocument);

    Collection<T> getAllThatMatches(T matchDocument);

    Path addDocument(T document);

    Path updateDocument(T documentCriteria, T updatedDocument);

    int deleteAllThatMatches(T matchDocument);

    Collection<T> getAll();
}

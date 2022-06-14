package com.atypon.nosql.database.collection;

import com.atypon.nosql.database.document.Document;

import java.io.IOException;
import java.util.Collection;

public interface IndexedDocumentsCollection<T extends Document<?>> extends DocumentsCollection<T> {
    void createIndex(T matchDocument) throws IOException;

    void deleteIndex(T matchDocument) throws NoSuchIndexException;

    Collection<T> getIndexes();
}

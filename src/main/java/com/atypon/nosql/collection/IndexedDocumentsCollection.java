package com.atypon.nosql.collection;

import com.atypon.nosql.document.Document;

import java.io.IOException;
import java.util.Collection;

public interface IndexedDocumentsCollection<T extends Document<?>> extends DocumentsCollection<T> {
    void createIndex(T matchDocument) throws IOException;

    void deleteIndex(T matchDocument) throws NoSuchIndexException;

    Collection<T> getIndexes();
}

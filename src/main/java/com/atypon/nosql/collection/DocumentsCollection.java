package com.atypon.nosql.collection;

import com.atypon.nosql.document.Document;

import javax.naming.directory.SchemaViolationException;
import java.io.IOException;
import java.util.Collection;

public interface DocumentsCollection<T extends Document<?>> {
    boolean contains(T matchDocument) throws IOException;

    Collection<T> get(T matchDocument) throws IOException;

    void put(T document) throws IOException, SchemaViolationException;

    void remove(T matchDocument) throws IOException;

    Collection<T> getAll() throws IOException;
}

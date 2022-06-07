package com.atypon.nosql.collection;

import com.atypon.nosql.document.Document;

import javax.naming.directory.SchemaViolationException;
import java.io.IOException;
import java.util.Collection;

public interface DocumentsCollection<T extends Document<?>> {
    boolean contains(T bound) throws IOException;

    Collection<T> get(T bound) throws IOException;

    void put(T document) throws IOException, SchemaViolationException;

    void remove(T bound) throws IOException;

    Collection<T> readAll() throws IOException;
}

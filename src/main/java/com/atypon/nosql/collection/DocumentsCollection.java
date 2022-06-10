package com.atypon.nosql.collection;

import com.atypon.nosql.document.Document;

import javax.naming.directory.SchemaViolationException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;

public interface DocumentsCollection<T extends Document<?>> {
    boolean contains(T matchDocument) throws IOException;

    Collection<T> getAllThatMatches(T matchDocument) throws IOException;

    Path put(T document) throws IOException;

    void remove(T matchDocument) throws IOException;

    Collection<T> getAll() throws IOException;
}

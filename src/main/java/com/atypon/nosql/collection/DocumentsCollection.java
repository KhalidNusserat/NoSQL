package com.atypon.nosql.collection;

import com.atypon.nosql.document.Document;
import com.atypon.nosql.document.ObjectID;

import javax.naming.directory.SchemaViolationException;
import java.io.IOException;
import java.util.Collection;

public interface DocumentsCollection<T extends Document<?>> {
    boolean containsID(ObjectID id);

    T get(ObjectID id) throws IOException;

    void put(ObjectID id, T document) throws IOException, SchemaViolationException;

    void remove(ObjectID id) throws IOException;

    Collection<T> readAll() throws IOException;
}

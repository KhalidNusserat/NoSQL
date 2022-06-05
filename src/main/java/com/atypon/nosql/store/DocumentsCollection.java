package com.atypon.nosql.store;

import com.atypon.nosql.store.exceptions.ItemNotFoundException;

import java.io.IOException;
import java.util.Collection;

public interface DocumentsCollection {
    boolean containsKey(String id);

    String get(String id) throws ItemNotFoundException, IOException;

    void put(String id, String content) throws Exception;

    void remove(String id) throws ItemNotFoundException, IOException;

    void clear() throws IOException;

    Collection<String> readAll() throws IOException;
}

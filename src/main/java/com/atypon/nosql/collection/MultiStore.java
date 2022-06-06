package com.atypon.nosql.collection;

import java.io.IOException;
import java.util.Collection;

public interface MultiStore {
    boolean contains(String collection, String id);

    void store(String collection, String id, String content) throws Exception;

    String read(String collection, String id) throws IOException, CollectionNotFoundException, ItemNotFoundException;

    void remove(String collection, String id) throws IOException, ItemNotFoundException;

    Collection<String> readCollection(String collection) throws IOException, CollectionNotFoundException;

    void removeCollection(String collection) throws CollectionNotFoundException, IOException;
}

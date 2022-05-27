package com.atypon.nosql.store;

import java.io.IOException;
import java.util.List;

public interface Store {
    boolean contains(String collection, String id);

    void store(String collection, String id, String content) throws Exception;

    String read(String collection, String id) throws IOException, AliasNotFoundException;

    void remove(String collection, String id) throws AliasNotFoundException, IOException;

    List<String> readCollection(String collection) throws IOException, CollectionNotFoundException;
}

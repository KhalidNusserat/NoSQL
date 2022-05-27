package com.atypon.nosql.store;

import java.io.IOException;

public interface Store {
    boolean containsAlias(String collection, String id);

    void store(String collection, String id, String content) throws Exception;

    String read(String collection, String name) throws IOException, AliasNotFoundException;
}

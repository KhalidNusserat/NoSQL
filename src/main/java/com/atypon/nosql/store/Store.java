package com.atypon.nosql.store;

import java.io.IOException;

public interface Store extends Iterable<StoredText> {
    boolean containsKey(String id);

    StoredText get(String id) throws ItemNotFoundException;

    void put(String id, String content) throws Exception;

    void remove(String id) throws ItemNotFoundException, IOException;

    void removeAll() throws IOException;
}

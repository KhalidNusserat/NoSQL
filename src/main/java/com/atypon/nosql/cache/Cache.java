package com.atypon.nosql.cache;

import com.atypon.nosql.store.ItemNotFoundException;

public interface Cache<CachedElement> {
    void put(String id, CachedElement element);

    CachedElement get(String id) throws ItemNotFoundException;

    boolean contains(String id);

    void remove(String id);

    void removeAll();
}

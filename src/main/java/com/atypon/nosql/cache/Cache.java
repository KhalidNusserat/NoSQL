package com.atypon.nosql.cache;

public interface Cache<CachedElement> {
    void add(String alias, CachedElement element);

    CachedElement get(String alias);
}

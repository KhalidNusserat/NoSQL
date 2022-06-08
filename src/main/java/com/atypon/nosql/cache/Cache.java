package com.atypon.nosql.cache;

import java.util.Optional;

public interface Cache<K, V> {
    void put(K key, V element);

    Optional<V> get(K key);

    void clear();

    boolean containsKey(K key);
}

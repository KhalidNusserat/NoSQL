package com.atypon.nosql.utils;

import java.util.Set;

public interface ReversedMap<K, V> {
    void put(K key, V value);

    void remove(K key);

    void clear();

    Set<K> getFromValue(V value);

    boolean containsValue(V value);
}

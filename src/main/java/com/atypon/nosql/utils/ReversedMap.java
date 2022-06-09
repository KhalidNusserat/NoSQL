package com.atypon.nosql.utils;

import java.util.Set;

public interface ReversedMap<K, V> {
    void put(K key, V value);

    void putIfAbsent(K key, V value);

    void removeByKey(K key);

    void removeByValue(V value);

    void clear();

    Set<K> getFromValue(V value);

    boolean containsValue(V value);
}

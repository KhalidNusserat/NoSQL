package com.atypon.nosql.index;

import java.util.Set;

public interface FieldIndex<K, V> {
    void put(K key, V value);

    void remove(K key);

    void clear();

    Set<K> getFromValue(V value);

    boolean containsValue(V value);
}

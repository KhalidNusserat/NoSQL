package com.atypon.nosql.index;

import java.util.Collection;
import java.util.Optional;

public interface FieldIndex<K, V> {
    void put(K key, V value);

    void remove(K key);

    void clear();

    Collection<K> getFromValue(V value);

    boolean containsKey(K key);
}

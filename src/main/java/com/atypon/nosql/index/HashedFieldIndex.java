package com.atypon.nosql.index;

import com.google.gson.annotations.JsonAdapter;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@JsonAdapter(HashedFieldIndexJsonAdapter.class)
public class HashedFieldIndex<K, V> implements FieldIndex<K, V> {
    final Map<V, Set<K>> valueToKeys;

    final Map<K, V> keyToValue;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    HashedFieldIndex(Map<K, V> keyToValue, Map<V, Set<K>> valueToKeys) {
        this.keyToValue = keyToValue;
        this.valueToKeys = valueToKeys;
    }

    public HashedFieldIndex() {
        keyToValue = new HashMap<>();
        valueToKeys = new HashMap<>();
    }

    @Override
    public void put(K key, V value) {
        lock.writeLock().lock();
        valueToKeys.putIfAbsent(value, new HashSet<>());
        valueToKeys.get(value).add(key);
        keyToValue.put(key, value);
        lock.writeLock().unlock();
    }

    @Override
    public void remove(K key) {
        lock.writeLock().lock();
        V value = keyToValue.get(key);
        keyToValue.remove(key);
        valueToKeys.get(value).remove(key);
        lock.writeLock().unlock();
    }

    @Override
    public void clear() {
        lock.writeLock().lock();
        keyToValue.clear();
        valueToKeys.clear();
        lock.writeLock().unlock();
    }

    @Override
    public Collection<K> getFromValue(V value) {
        lock.readLock().lock();
        Collection<K> keys = valueToKeys.getOrDefault(value, Set.of());
        lock.readLock().unlock();
        return keys;
    }

    @Override
    public boolean containsKey(K key) {
        return keyToValue.containsKey(key);
    }
}

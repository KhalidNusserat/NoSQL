package com.atypon.nosql.index;

import com.google.gson.annotations.Expose;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class HashedFieldIndex<K, V> implements FieldIndex<K, V> {
    @Expose
    final Map<V, Set<K>> valueToKeys;

    @Expose
    final Map<K, V> keyToValue;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

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
    public Set<K> getFromValue(V value) {
        lock.readLock().lock();
        Set<K> keys = valueToKeys.getOrDefault(value, Set.of());
        lock.readLock().unlock();
        return keys;
    }

    @Override
    public boolean containsValue(V value) {
        return valueToKeys.containsKey(value);
    }
}

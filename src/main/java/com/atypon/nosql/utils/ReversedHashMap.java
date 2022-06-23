package com.atypon.nosql.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReversedHashMap<K, V> implements ReversedMap<K, V> {
    private final Map<V, Set<K>> valueToKeys;

    private final Map<K, V> keyToValue;

    private transient final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public ReversedHashMap() {
        keyToValue = new HashMap<>();
        valueToKeys = new HashMap<>();
    }

    public ReversedHashMap(ReversedHashMap<K, V> other) {
        this.valueToKeys = new HashMap<>(other.valueToKeys);
        this.keyToValue = new HashMap<>(other.keyToValue);
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
    public void putIfAbsent(K key, V value) {
        lock.writeLock().lock();
        if (!keyToValue.containsKey(key)) {
            put(key, value);
        }
        lock.writeLock().unlock();
    }

    @Override
    public void removeByKey(K key) {
        lock.writeLock().lock();
        V value = keyToValue.get(key);
        keyToValue.remove(key);
        valueToKeys.get(value).remove(key);
        lock.writeLock().unlock();
    }

    @Override
    public void removeByValue(V value) {
        lock.writeLock().lock();
        for (K key : valueToKeys.get(value)) {
            keyToValue.remove(key);
        }
        valueToKeys.remove(value);
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

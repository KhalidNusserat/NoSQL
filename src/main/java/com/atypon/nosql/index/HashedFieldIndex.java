package com.atypon.nosql.index;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class HashedFieldIndex<K, V> implements FieldIndex<K, V> {
    private final Map<V, Set<K>> valueToKeys = new HashMap<>();

    private final Map<K, V> keyToValue = new HashMap<>();

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

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
    public Optional<V> getFromKey(K key) {
        lock.readLock().lock();
        V value = keyToValue.get(key);
        lock.readLock().unlock();
        return Optional.ofNullable(value);
    }

    @Override
    public Collection<K> getFromValue(V value) {
        lock.readLock().lock();
        Collection<K> keys = valueToKeys.getOrDefault(value, Set.of());
        lock.readLock().unlock();
        return keys;
    }
}

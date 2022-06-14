package com.atypon.nosql.database.cache;

import com.atypon.nosql.database.utils.DoublyLinkedList;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.atypon.nosql.database.utils.DoublyLinkedList.Node;

public class LRUCache<K, V> implements Cache<K, V> {
    private final DoublyLinkedList<CacheElement<K, V>> linkedList = new DoublyLinkedList<>();

    private final Map<K, Node<CacheElement<K, V>>> map = new ConcurrentHashMap<>();

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private final int capacity;

    public LRUCache(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public void put(K key, V value) {
        lock.writeLock().lock();
        Node<CacheElement<K, V>> node = Node.fromValue(new CacheElement<>(key, value));
        if (map.containsKey(key)) {
            linkedList.remove(map.get(key));
        }
        if (linkedList.size() >= capacity) {
            removeLeastUsed();
        }
        linkedList.add(node);
        map.put(key, node);
        lock.writeLock().unlock();
    }

    @Override
    public Optional<V> get(K key) {
        lock.readLock().lock();
        Optional<V> result = Optional.empty();
        if (map.containsKey(key)) {
            linkedList.moveToFront(map.get(key));
            result = Optional.of(map.get(key).value().value);
        }
        lock.readLock().unlock();
        return result;
    }

    @Override
    public void clear() {
        lock.writeLock().lock();
        map.clear();
        linkedList.clear();
        lock.writeLock().unlock();
    }

    @Override
    public boolean containsKey(K key) {
        lock.readLock().lock();
        boolean contains = map.containsKey(key);
        lock.readLock().unlock();
        return contains;
    }

    @Override
    public void remove(K key) {
        lock.writeLock().lock();
        linkedList.remove(map.get(key));
        map.remove(key);
        lock.writeLock().unlock();
    }

    private void removeLeastUsed() {
        lock.writeLock().lock();
        Node<CacheElement<K, V>> leastUsed = linkedList.getFront();
        linkedList.remove(leastUsed);
        map.remove(leastUsed.value().key);
        lock.writeLock().unlock();
    }

    public static class CacheElement<K, V> {
        private final K key;

        private final V value;

        public CacheElement(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
}

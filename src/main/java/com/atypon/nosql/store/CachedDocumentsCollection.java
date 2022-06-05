package com.atypon.nosql.store;

import com.atypon.nosql.cache.Cache;
import com.atypon.nosql.store.exceptions.ItemNotFoundException;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CachedDocumentsCollection implements DocumentsCollection {
    private final Cache<String, String> cache;

    private final DocumentsCollection documentsCollection;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public CachedDocumentsCollection(Cache<String, String> cache, DocumentsCollection documentsCollection) {
        this.cache = cache;
        this.documentsCollection = documentsCollection;
    }

    @Override
    public boolean containsKey(String id) {
        lock.readLock().lock();
        boolean result = documentsCollection.containsKey(id);
        lock.readLock().unlock();
        return result;
    }

    @Override
    public String get(String id) throws ItemNotFoundException, IOException {
        lock.readLock().lock();
        String result = cache.get(id).orElse(documentsCollection.get(id));
        lock.readLock().unlock();
        return result;
    }

    @Override
    public void put(String id, String content) throws Exception {
        lock.writeLock().lock();
        documentsCollection.put(id, content);
        lock.writeLock().unlock();
    }

    @Override
    public void remove(String id) throws ItemNotFoundException, IOException {
        lock.writeLock().lock();
        documentsCollection.remove(id);
        lock.writeLock().unlock();
    }

    @Override
    public void clear() throws IOException {
        lock.writeLock().lock();
        cache.clear();
        documentsCollection.clear();
        lock.writeLock().unlock();
    }

    @Override
    public Collection<String> readAll() throws IOException {
        lock.readLock().lock();
        Collection<String> result = documentsCollection.readAll();
        lock.readLock().unlock();
        return result;
    }
}

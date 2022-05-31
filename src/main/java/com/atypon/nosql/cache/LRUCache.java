package com.atypon.nosql.cache;

import com.atypon.nosql.store.ItemNotFoundException;

import java.util.HashMap;
import java.util.Map;

public class LRUCache<CachedElement> implements Cache<CachedElement> {
    private final Map<String, CachedElement> map = new HashMap<>();

    private final LRUQueue<String> lruQueue = new LRUQueue<>();

    private final int capacity;

    public LRUCache(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public void put(String id, CachedElement element) {
        synchronized (map) {
            if (!map.containsKey(id)) {
                if (lruQueue.size() >= capacity) {
                    map.remove(lruQueue.removeLeastUsed());
                }
                map.put(id, element);
                lruQueue.add(id);
            }
        }
    }

    @Override
    public CachedElement get(String id) throws ItemNotFoundException {
        synchronized (map) {
            if (!map.containsKey(id)) {
                throw new ItemNotFoundException("Could not find item in cache: " + id);
            }
            lruQueue.use(id);
            return map.get(id);
        }
    }
}

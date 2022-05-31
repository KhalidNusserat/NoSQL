package com.atypon.nosql.cache;

import com.atypon.nosql.store.ItemNotFoundException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LRUCacheTest {

    @Test
    void putAndGet() throws ItemNotFoundException {
        Cache<String> cache = new LRUCache<>(3);
        cache.put("1", "Cat");
        cache.put("2", "Dog");
        cache.put("2", "Hamster");
        cache.put("3", "Duck");

        assertEquals("Cat", cache.get("1"));
        assertEquals("Cat", cache.get("1"));
        assertEquals("Duck", cache.get("3"));

        cache.put("4", "Kitten");

        assertThrows(ItemNotFoundException.class, () -> cache.get("2"));

        assertEquals("Kitten", cache.get("4"));
    }
}
package com.atypon.nosql.cache;

import com.atypon.nosql.store.exceptions.ItemNotFoundException;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class LRUCacheTest {

    @Test
    void putAndGet() throws ItemNotFoundException {
        Cache<String, String> cache = new LRUCache<>(3);
        cache.put("1", "Cat");
        cache.put("2", "Dog");
        cache.put("2", "Hamster");
        cache.put("3", "Duck");

        assertEquals("Cat", cache.get("1").orElseThrow());
        assertEquals("Cat", cache.get("1").orElseThrow());
        assertEquals("Duck", cache.get("3").orElseThrow());

        cache.put("4", "Kitten");

        assertThrows(NoSuchElementException.class, () -> cache.get("2").get());

        assertEquals("Kitten", cache.get("4").orElseThrow());
    }
}
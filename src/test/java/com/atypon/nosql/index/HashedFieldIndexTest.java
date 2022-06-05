package com.atypon.nosql.index;

import com.google.common.collect.Range;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class HashedFieldIndexTest {

    @Test
    void putAndGet() throws InterruptedException {
        HashedFieldIndex<Integer, Integer> index = new HashedFieldIndex<>();
        index.put(1, 2);
        index.put(2, 2);
        index.put(3, 1);
        index.put(4, 2);
        assertEquals(2, index.getFromKey(1).orElseThrow());
        assertEquals(2, index.getFromKey(2).orElseThrow());
        assertEquals(1, index.getFromKey(3).orElseThrow());
        assertEquals(2, index.getFromKey(4).orElseThrow());
        assertTrue(List.of(1, 2, 4).containsAll(index.getFromValue(2)));
        assertTrue(List.of(3).containsAll(index.getFromValue(1)));

        ExecutorService service = Executors.newCachedThreadPool();
        for (int i = 5; i <= 100; i++) {
            int finalI = i;
            service.submit(() -> index.put(finalI, 0));
        }

        Thread.sleep(500);

        assertTrue(Range.closed(5, 100).containsAll(index.getFromValue(0)));
    }

    @Test
    void remove() {
        HashedFieldIndex<Integer, Integer> index = new HashedFieldIndex<>();
        index.put(1, 2);
        index.put(2, 2);
        index.put(3, 1);
        index.put(4, 2);
        index.remove(1);
        assertTrue(List.of(2, 4).containsAll(index.getFromValue(2)));
        assertTrue(index.getFromKey(1).isEmpty());
    }

    @Test
    void clear() {
        HashedFieldIndex<Integer, Integer> index = new HashedFieldIndex<>();
        index.put(1, 2);
        index.put(2, 2);
        index.put(3, 1);
        index.put(4, 2);
        index.clear();
        assertTrue(index.getFromValue(2).isEmpty());
        assertTrue(index.getFromValue(1).isEmpty());
    }
}
package com.atypon.nosql.utils;

import com.atypon.nosql.utils.ReversedHashMap;
import com.google.common.collect.Range;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ReversedHashMapTest {

    @Test
    void putAndGet() throws InterruptedException {
        ReversedHashMap<Integer, Integer> index = new ReversedHashMap<>();
        index.put(1, 2);
        index.put(2, 2);
        index.put(3, 1);
        index.put(4, 2);
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
        ReversedHashMap<Integer, Integer> index = new ReversedHashMap<>();
        index.put(1, 2);
        index.put(2, 2);
        index.put(3, 1);
        index.put(4, 2);
        index.removeByKey(1);
        assertTrue(List.of(2, 4).containsAll(index.getFromValue(2)));
    }

    @Test
    void clear() {
        ReversedHashMap<Integer, Integer> index = new ReversedHashMap<>();
        index.put(1, 2);
        index.put(2, 2);
        index.put(3, 1);
        index.put(4, 2);
        index.clear();
        assertTrue(index.getFromValue(2).isEmpty());
        assertTrue(index.getFromValue(1).isEmpty());
    }
}
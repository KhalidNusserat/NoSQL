package com.atypon.nosql.store;

import com.atypon.nosql.cache.LRUCache;
import com.atypon.nosql.utils.Cleanup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class CachedDocumentsCollectionTest {
    @AfterEach
    public void cleanup() {
        Cleanup.cleanupDirectory(new File("./db/"));
    }

    @Test
    public void cachedPerformance() throws IOException, ClassNotFoundException, InterruptedException, ExecutionException {
        CachedDocumentsCollection cachedStore = new CachedDocumentsCollection(new LRUCache<>(100), new SimpleDocumentsCollection("./db/"));
        List<Callable<String>> list = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            String index = Integer.toString(i);
            list.add(() -> {
               cachedStore.put(index, index.repeat(10000));
               return null;
            });
        }
        ExecutorService service = Executors.newCachedThreadPool();
        service.invokeAll(list);
        List<Callable<String>> callables = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            String index = Integer.toString(i);
            callables.add(() -> {
                return cachedStore.get(index);
            });
        }
        for (int i = 0; i < 1000; i++) {
            String index = Integer.toString(i);
            callables.add(() -> {
                return cachedStore.get(index);
            });
        }
        List<Future<String>> futures = service.invokeAll(callables);
        for (int i = 0; i < 1000; i++) {
            assertEquals(Integer.toString(i).repeat(10000), futures.get(i).get());
        }
        for (int i = 1000; i < 2000; i++) {
            assertEquals(Integer.toString(i - 1000).repeat(10000), futures.get(i).get());
        }
    }

    @Test
    public void uncachedPerformance() throws IOException, ClassNotFoundException, InterruptedException, ExecutionException {
        DocumentsCollection documentsCollection = new SimpleDocumentsCollection("./db/");
        List<Callable<String>> list = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            String index = Integer.toString(i);
            list.add(() -> {
                documentsCollection.put(index, index.repeat(10000));
                return null;
            });
        }
        ExecutorService service = Executors.newCachedThreadPool();
        service.invokeAll(list);
        List<Callable<String>> callables = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            String index = Integer.toString(i);
            callables.add(() -> {
                return documentsCollection.get(index);
            });
        }
        for (int i = 0; i < 1000; i++) {
            String index = Integer.toString(i);
            callables.add(() -> {
                return documentsCollection.get(index);
            });
        }
        List<Future<String>> futures = service.invokeAll(callables);
        for (int i = 0; i < 1000; i++) {
            assertEquals(Integer.toString(i).repeat(10000), futures.get(i).get());
        }
        for (int i = 1000; i < 2000; i++) {
            assertEquals(Integer.toString(i - 1000).repeat(10000), futures.get(i).get());
        }
    }
}
package com.atypon.nosql.store;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class HashedStoreTest {
    @Test
    public void storeAndRead() throws Exception {
        Store store = new HashedStore();
        store.store(
                "a",
                "1",
                "test1"
        );
        assertEquals(store.read("a", "1"), "test1");
        store.store(
                "b",
                "1",
                "test2"
        );
        assertEquals(store.read("a", "1"), "test1");
        assertEquals(store.read("b", "1"), "test2");
        store.store(
                "a",
                "1",
                "test3"
        );
        assertEquals(store.read("a", "1"), "test3");
    }

    @Test
    public void multithreadingStoreAndRead()
            throws IOException, ClassNotFoundException, InterruptedException, ExecutionException {
        ExecutorService service = Executors.newCachedThreadPool();
        Store store = new HashedStore();
        List<Callable<String>> callables = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            String index = Integer.toString(i);
            callables.add(
                    () -> {
                        try {
                            store.store("a", index, index);
                            return store.read("a", index);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
            );
        }
        List<Future<String>> futures = service.invokeAll(callables);
        for (int i = 0; i < 500; i++) {
            String index = Integer.toString(i);
            assertEquals(index, futures.get(i).get());
        }
    }

    @Test
    public void readCollection() throws Exception {
        Store store = new HashedStore();
        store.store("pets", "1", "cat");
        store.store("pets", "2", "dog");
        store.store("pets", "3", "parrot");
        store.store("pets", "2", "kitten");
        store.store("pets", "4", "snake");
        store.remove("pets", "4");
        assertTrue(List.of("cat", "kitten", "parrot").containsAll(store.readCollection("pets")));
        assertFalse(store.readCollection("pets").contains("snake"));
    }

    @Test
    public void remove() throws Exception {
        Store store = new HashedStore();
        store.store("a", "1", "test");
        assertEquals("test", store.read("a", "1"));
        store.remove("a", "1");
        assertFalse(store.contains("a", "1"));
        assertEquals(1, new File("./db/").listFiles().length);
        assertEquals("unique.index", new File("./db/").listFiles()[0].getName());
    }

    @AfterEach
    public void cleanup() {
        File dir = new File("./db/");
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            file.delete();
        }
        dir.delete();
    }
}
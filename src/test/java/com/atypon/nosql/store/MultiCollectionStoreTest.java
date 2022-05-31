package com.atypon.nosql.store;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class MultiCollectionStoreTest {
    private void cleanupDirectory(@NotNull File directory) {
        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.isFile()) {
                boolean ignored = file.delete();
            } else {
                cleanupDirectory(file);
            }
        }
        boolean ignored = directory.delete();
    }

    @AfterEach
    public void cleanup() {
        cleanupDirectory(new File("./db/"));
    }

    @Test
    public void storeAndRead() throws Exception {
        MultiCollectionStore multiCollectionStore = new MultiCollectionStore();
        multiCollectionStore.createNewCollection("a");
        multiCollectionStore.createNewCollection("b");
        multiCollectionStore.store(
                "a",
                "1",
                "test1"
        );
        assertEquals(multiCollectionStore.read("a", "1"), "test1");
        multiCollectionStore.store(
                "b",
                "1",
                "test2"
        );
        assertEquals(multiCollectionStore.read("a", "1"), "test1");
        assertEquals(multiCollectionStore.read("b", "1"), "test2");
        multiCollectionStore.store(
                "a",
                "1",
                "test3"
        );
        multiCollectionStore.store(
                "a",
                "1",
                "test4"
        );
        multiCollectionStore.store(
                "a",
                "1",
                "test5"
        );
        assertEquals(multiCollectionStore.read("a", "1"), "test5");
    }

    @Test
    public void multithreadingStoreAndRead()
            throws InterruptedException, ExecutionException, IOException, ClassNotFoundException {
        ExecutorService service = Executors.newCachedThreadPool();
        MultiCollectionStore multiCollectionStore = new MultiCollectionStore();
        multiCollectionStore.createNewCollection("a");
        List<Callable<String>> callables = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            String index = Integer.toString(i);
            callables.add(
                    () -> {
                        try {
                            multiCollectionStore.store("a", index, index);
                            return multiCollectionStore.read("a", index);
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
        MultiCollectionStore multiCollectionStore = new MultiCollectionStore();
        multiCollectionStore.createNewCollection("pets");
        multiCollectionStore.store("pets", "1", "cat");
        multiCollectionStore.store("pets", "2", "dog");
        multiCollectionStore.store("pets", "3", "parrot");
        multiCollectionStore.store("pets", "2", "kitten");
        multiCollectionStore.store("pets", "4", "snake");
        multiCollectionStore.remove("pets", "4");
        assertTrue(List.of("cat", "kitten", "parrot").containsAll(multiCollectionStore.readCollection("pets")));
        assertFalse(multiCollectionStore.readCollection("pets").contains("snake"));
    }

    @Test
    public void remove() throws Exception {
        MultiCollectionStore multiCollectionStore = new MultiCollectionStore();
        multiCollectionStore.createNewCollection("a");
        multiCollectionStore.store("a", "1", "test");
        assertEquals("test", multiCollectionStore.read("a", "1"));
        multiCollectionStore.remove("a", "1");
        assertFalse(multiCollectionStore.contains("a", "1"));
        assertEquals(1, Objects.requireNonNull(new File("./db/a").listFiles()).length);
        assertTrue(new File("./db/a/unique.index").exists());
    }
}
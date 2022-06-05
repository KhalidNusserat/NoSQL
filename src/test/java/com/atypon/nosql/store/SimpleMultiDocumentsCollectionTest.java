package com.atypon.nosql.store;

import com.google.common.io.MoreFiles;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class SimpleMultiDocumentsCollectionTest {
    @AfterEach
    @SuppressWarnings("UnstableApiUsage")
    public void cleanup() throws IOException {
        MoreFiles.deleteDirectoryContents(Path.of("./db/"));
    }

    @Test
    public void storeAndRead() throws Exception {
        SimpleMultiStore simpleStore = new SimpleMultiStore();
        simpleStore.createNewCollection("a");
        simpleStore.createNewCollection("b");
        simpleStore.store(
                "a",
                "1",
                "test1"
        );
        assertEquals(simpleStore.read("a", "1"), "test1");
        simpleStore.store(
                "b",
                "1",
                "test2"
        );
        assertEquals(simpleStore.read("a", "1"), "test1");
        assertEquals(simpleStore.read("b", "1"), "test2");
        simpleStore.store(
                "a",
                "1",
                "test3"
        );
        simpleStore.store(
                "a",
                "1",
                "test4"
        );
        simpleStore.store(
                "a",
                "1",
                "test5"
        );
        assertEquals(simpleStore.read("a", "1"), "test5");
    }

    @Test
    public void multithreadingStoreAndRead()
            throws InterruptedException, ExecutionException, IOException, ClassNotFoundException {
        ExecutorService service = Executors.newCachedThreadPool();
        SimpleMultiStore simpleStore = new SimpleMultiStore();
        simpleStore.createNewCollection("a");
        List<Callable<String>> callables = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            String index = Integer.toString(i);
            callables.add(
                    () -> {
                        try {
                            simpleStore.store("a", index, index);
                            return simpleStore.read("a", index);
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
        SimpleMultiStore simpleStore = new SimpleMultiStore();
        simpleStore.createNewCollection("pets");
        simpleStore.store("pets", "1", "cat");
        simpleStore.store("pets", "2", "dog");
        simpleStore.store("pets", "3", "parrot");
        simpleStore.store("pets", "2", "kitten");
        simpleStore.store("pets", "4", "snake");
        simpleStore.remove("pets", "4");
        Collection<String> allElements = simpleStore.readCollection("pets");
        assertTrue(List.of("cat", "kitten", "parrot").containsAll(allElements));
        assertFalse(simpleStore.readCollection("pets").contains("snake"));
    }

    @Test
    public void remove() throws Exception {
        SimpleMultiStore simpleStore = new SimpleMultiStore();
        simpleStore.createNewCollection("a");
        simpleStore.store("a", "1", "test");
        assertEquals("test", simpleStore.read("a", "1"));
        simpleStore.remove("a", "1");
        assertFalse(simpleStore.contains("a", "1"));
        assertEquals(1, Objects.requireNonNull(new File("./db/a").listFiles()).length);
        assertTrue(new File("./db/a/unique.index").exists());
    }
}
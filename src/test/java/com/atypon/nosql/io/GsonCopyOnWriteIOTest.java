package com.atypon.nosql.io;

import com.google.common.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GsonCopyOnWriteIOTest {
    private final Path testDirectory = Path.of("./test");

    private final Type listType = new TypeToken<List<String>>() {}.getType();

    @BeforeEach
    void setUp() throws IOException {
        Files.createDirectories(testDirectory);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.walk(testDirectory).filter(Files::isRegularFile).forEach(path -> {
            try {
                Files.delete(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        Files.delete(testDirectory);
    }

    @Test
    void writeAndRead() throws IOException {
        CopyOnWriteIO io = new GsonCopyOnWriteIO();
        Path filepath = io.write(List.of("Khalid"), listType, testDirectory, ".json");
        assertEquals(List.of("Khalid"), io.read(filepath, listType));
    }

    @Test
    void delete() throws IOException, InterruptedException {
        CopyOnWriteIO io = new GsonCopyOnWriteIO();
        Path filepath = io.write(List.of("Delete me"), listType, testDirectory, ".json");
        io.delete(filepath);
        Thread.sleep(200);
        assertEquals(1, Files.walk(testDirectory).toList().size());
    }

    @Test
    void update() throws IOException, InterruptedException {
        CopyOnWriteIO io = new GsonCopyOnWriteIO();
        Type listType = new TypeToken<List<String>>() {
        }.getType();
        Path filepath = io.write(List.of("Old"), listType, testDirectory, ".json");
        filepath = io.update(List.of("New"), listType, filepath, ".json");
        Thread.sleep(200);
        assertEquals(List.of("New"), io.read(filepath, listType));
        assertEquals(2, Files.walk(testDirectory).toList().size());
    }
}
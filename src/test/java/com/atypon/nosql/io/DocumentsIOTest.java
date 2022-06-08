package com.atypon.nosql.io;

import com.atypon.nosql.gsondocument.GsonDocument;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class DocumentsIOTest {
    private final Path testDirectory = Path.of("./test");

    private final GsonDocument khalid;

    private final GsonDocument john;

    protected DocumentsIOTest() {
        JsonObject khalidObject = new JsonObject();
        khalidObject.addProperty("name", "Khalid");
        khalidObject.addProperty("content", "Kh".repeat(1000000));
        khalid = GsonDocument.of(khalidObject);
        JsonObject johnObject = new JsonObject();
        johnObject.addProperty("name", "John");
        johnObject.addProperty("content", "Jo".repeat(1000000));
        john = GsonDocument.of(johnObject);
    }

    public abstract DocumentsIO<GsonDocument> create();

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
        DocumentsIO<GsonDocument> io = create();
        Path filepath = io.write(khalid, testDirectory);
        assertEquals(khalid, io.read(filepath).orElseThrow());
    }

    @Test
    void delete() throws IOException, InterruptedException {
        DocumentsIO<GsonDocument> io = create();
        Path filepath = io.write(khalid, testDirectory);
        io.delete(filepath);
        Thread.sleep(200);
        assertEquals(1, Files.walk(testDirectory).toList().size());
    }

    @Test
    void update() throws IOException, InterruptedException {
        DocumentsIO<GsonDocument> io = create();
        Path filepath = io.write(john, testDirectory);
        filepath = io.update(khalid, filepath);
        Thread.sleep(200);
        assertEquals(khalid, io.read(filepath).orElseThrow());
        assertEquals(2, Files.walk(testDirectory).toList().size());
    }
}

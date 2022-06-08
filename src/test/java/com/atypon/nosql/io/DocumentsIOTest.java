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

public abstract class DocumentsIOTest {
    private final Path testDirectory = Path.of("./test");

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
        JsonObject personObject = new JsonObject();
        personObject.addProperty("name", "Khalid");
        GsonDocument person = GsonDocument.of(personObject);
        Path filepath = io.write(person, testDirectory);
        assertEquals(person, io.read(filepath).orElseThrow());
    }

    @Test
    void delete() throws IOException, InterruptedException {
        DocumentsIO<GsonDocument> io = create();
        JsonObject person = new JsonObject();
        person.addProperty("name", "Khalid");
        Path filepath = io.write(GsonDocument.of(person), testDirectory);
        io.delete(filepath);
        Thread.sleep(200);
        assertEquals(1, Files.walk(testDirectory).toList().size());
    }

    @Test
    void update() throws IOException, InterruptedException {
        DocumentsIO<GsonDocument> io = create();
        JsonObject person = new JsonObject();
        person.addProperty("name", "John");
        Path filepath = io.write(GsonDocument.of(person), testDirectory);
        person.addProperty("name", "Khalid");
        GsonDocument khalid = GsonDocument.of(person);
        filepath = io.update(khalid, filepath);
        Thread.sleep(200);
        assertEquals(khalid, io.read(filepath).orElseThrow());
        assertEquals(2, Files.walk(testDirectory).toList().size());
    }
}

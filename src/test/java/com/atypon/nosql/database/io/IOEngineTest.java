package com.atypon.nosql.database.io;

import com.atypon.nosql.database.document.DocumentFactory;
import com.atypon.nosql.database.document.ObjectIdGenerator;
import com.atypon.nosql.database.document.RandomObjectIdGenerator;
import com.atypon.nosql.database.gsondocument.GsonDocument;
import com.atypon.nosql.database.gsondocument.GsonDocumentFactory;
import com.atypon.nosql.database.utils.FileUtils;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class IOEngineTest {
    protected final Path testDirectory = Path.of("./test");

    protected final DocumentFactory documentFactory;

    private final GsonDocument khalid;

    private final GsonDocument john;

    protected IOEngineTest() {
        JsonObject khalidObject = new JsonObject();
        khalidObject.addProperty("name", "Khalid");
        khalidObject.addProperty("content", "Kh".repeat(10000000));
        khalid = GsonDocument.fromJsonObject(khalidObject);
        JsonObject johnObject = new JsonObject();
        johnObject.addProperty("name", "John");
        johnObject.addProperty("content", "Jo".repeat(10000000));
        john = GsonDocument.fromJsonObject(johnObject);
        ObjectIdGenerator idGenerator = new RandomObjectIdGenerator();
        documentFactory = new GsonDocumentFactory(idGenerator);
    }

    public abstract IOEngine create();

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
    void writeAndRead() {
        IOEngine io = create();
        Path filepath = io.write(khalid, testDirectory);
        assertEquals(khalid, io.read(filepath).orElseThrow());
    }

    @Test
    void delete() throws InterruptedException {
        IOEngine io = create();
        Path filepath = io.write(khalid, testDirectory);
        io.delete(filepath);
        Thread.sleep(50);
        assertEquals(0, FileUtils.countFiles(testDirectory, 1));
    }

    @Test
    void update() throws InterruptedException {
        IOEngine io = create();
        Path filepath = io.write(john, testDirectory);
        filepath = io.update(khalid, filepath);
        assertEquals(khalid, io.read(filepath).orElseThrow());
        Thread.sleep(50);
        assertEquals(1, FileUtils.countFiles(testDirectory, 1));
    }
}

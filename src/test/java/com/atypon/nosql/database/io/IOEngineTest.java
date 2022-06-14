package com.atypon.nosql.database.io;

import com.atypon.nosql.database.document.DocumentGenerator;
import com.atypon.nosql.database.document.ObjectIdGenerator;
import com.atypon.nosql.database.document.RandomObjectIdGenerator;
import com.atypon.nosql.database.gsondocument.GsonDocument;
import com.atypon.nosql.database.gsondocument.GsonDocumentGenerator;
import com.atypon.nosql.database.utils.ExtraFileUtils;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class IOEngineTest {
    private final Path testDirectory = Path.of("./test");

    private final GsonDocument khalid;

    private final GsonDocument john;

    private final ObjectIdGenerator idGenerator = new RandomObjectIdGenerator();

    private final DocumentGenerator<GsonDocument> documentGenerator = new GsonDocumentGenerator(idGenerator);

    protected IOEngineTest() {
        JsonObject khalidObject = new JsonObject();
        khalidObject.addProperty("name", "Khalid");
        khalidObject.addProperty("content", "Kh".repeat(10000000));
        khalid = GsonDocument.fromJsonObject(khalidObject);
        JsonObject johnObject = new JsonObject();
        johnObject.addProperty("name", "John");
        johnObject.addProperty("content", "Jo".repeat(10000000));
        john = GsonDocument.fromJsonObject(johnObject);
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
    void writeAndRead() throws IOException {
        IOEngine io = create();
        Path filepath = io.write(khalid, testDirectory);
        assertEquals(khalid, io.read(filepath, documentGenerator).orElseThrow());
    }

    @Test
    void delete() throws IOException, InterruptedException {
        IOEngine io = create();
        Path filepath = io.write(khalid, testDirectory);
        io.delete(filepath);
        Thread.sleep(50);
        assertEquals(0, ExtraFileUtils.countFiles(testDirectory, 1));
    }

    @Test
    void update() throws IOException, InterruptedException {
        IOEngine io = create();
        Path filepath = io.write(john, testDirectory);
        filepath = io.update(khalid, filepath);
        assertEquals(khalid, io.read(filepath, documentGenerator).orElseThrow());
        Thread.sleep(50);
        assertEquals(1, ExtraFileUtils.countFiles(testDirectory, 1));
    }
}

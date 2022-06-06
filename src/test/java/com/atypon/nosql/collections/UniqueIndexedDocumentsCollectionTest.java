package com.atypon.nosql.collections;

import com.atypon.nosql.document.ObjectID;
import com.atypon.nosql.document.RandomObjectID;
import com.atypon.nosql.gsondocument.GsonDocument;
import com.atypon.nosql.gsondocument.GsonDocumentParser;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Person {
    public static JsonObject fromNameAndAge(String name, int age) {
        JsonObject person = new JsonObject();
        person.addProperty("name", name);
        person.addProperty("age", age);
        return person;
    }
}

class UniqueIndexedDocumentsCollectionTest {
    private final Path testDirectory = Path.of("./test");

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
    void putAndGet() throws IOException, InterruptedException {
        GsonDocumentParser parser = new GsonDocumentParser();
        DocumentsCollection<GsonDocument> collection = new UniqueIndexedDocumentsCollection<>(parser, testDirectory);
        JsonObject khalid = Person.fromNameAndAge("Khalid", 22);
        JsonObject hamza = Person.fromNameAndAge("Hamza", 22);
        JsonObject john = Person.fromNameAndAge("John", 43);
        ObjectID khalidID = new RandomObjectID();
        ObjectID hamzaID = new RandomObjectID();
        ObjectID johnID = new RandomObjectID();
        collection.put(khalidID, new GsonDocument(khalid));
        collection.put(hamzaID, new GsonDocument(hamza));
        collection.put(johnID, new GsonDocument(john));
        Thread.sleep(800);
        assertEquals("Khalid", collection.get(khalidID).get("name").getAsString());
        assertEquals("Hamza", collection.get(hamzaID).get("name").getAsString());
        assertEquals("John", collection.get(johnID).get("name").getAsString());
    }

    @Test
    void remove() {
    }

    @Test
    void readAll() {
    }
}
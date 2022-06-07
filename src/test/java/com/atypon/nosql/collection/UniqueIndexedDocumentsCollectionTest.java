package com.atypon.nosql.collection;

import com.atypon.nosql.document.ObjectID;
import com.atypon.nosql.document.RandomObjectID;
import com.atypon.nosql.gsondocument.GsonDocument;
import com.atypon.nosql.gsondocument.GsonDocumentParser;
import com.atypon.nosql.gsondocument.GsonDocumentSchema;
import com.atypon.nosql.keywordsparser.InvalidKeywordException;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.naming.directory.SchemaViolationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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

    private final GsonDocumentSchema documentSchema = new GsonDocumentSchema(
            "{name: \"string;required\", age: \"number;default(18)\"}"
    );

    UniqueIndexedDocumentsCollectionTest() throws InvalidKeywordException {
    }

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
    void putAndGet() throws IOException, InterruptedException, SchemaViolationException {
        GsonDocumentParser parser = new GsonDocumentParser();
        DocumentsCollection<GsonDocument> collection = new UniqueIndexedDocumentsCollection<>(documentSchema, parser, testDirectory);
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
    void remove() throws IOException, SchemaViolationException {
        GsonDocumentParser parser = new GsonDocumentParser();
        DocumentsCollection<GsonDocument> collection = new UniqueIndexedDocumentsCollection<>(documentSchema, parser, testDirectory);
        JsonObject khalid = Person.fromNameAndAge("Khalid", 22);
        JsonObject hamza = Person.fromNameAndAge("Hamza", 22);
        JsonObject john = Person.fromNameAndAge("John", 43);
        ObjectID khalidID = new RandomObjectID();
        ObjectID hamzaID = new RandomObjectID();
        ObjectID johnID = new RandomObjectID();
        collection.put(khalidID, new GsonDocument(khalid));
        collection.put(hamzaID, new GsonDocument(hamza));
        collection.put(johnID, new GsonDocument(john));
        collection.remove(johnID);
        assertFalse(collection.containsID(johnID));
    }

    @Test
    void readAll() throws IOException, SchemaViolationException {
        GsonDocumentParser parser = new GsonDocumentParser();
        DocumentsCollection<GsonDocument> collection = new UniqueIndexedDocumentsCollection<>(documentSchema, parser, testDirectory);
        JsonObject khalid = Person.fromNameAndAge("Khalid", 22);
        JsonObject hamza = Person.fromNameAndAge("Hamza", 22);
        JsonObject john = Person.fromNameAndAge("John", 43);
        ObjectID khalidID = new RandomObjectID();
        ObjectID hamzaID = new RandomObjectID();
        ObjectID johnID = new RandomObjectID();
        collection.put(khalidID, new GsonDocument(khalid));
        collection.put(hamzaID, new GsonDocument(hamza));
        collection.put(johnID, new GsonDocument(john));
        assertTrue(List.of("Khalid", "Hamza", "John").containsAll(
                collection.readAll().stream()
                        .map(gsonDocument -> gsonDocument.get("name").getAsString())
                        .toList()
        ));
    }
}
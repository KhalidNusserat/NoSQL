package com.atypon.nosql.collection;

import com.atypon.nosql.gsondocument.GsonDocument;
import com.atypon.nosql.gsondocument.GsonDocumentParser;
import com.atypon.nosql.gsondocument.GsonDocumentSchema;
import com.atypon.nosql.gsondocument.GsonMatchDocument;
import com.atypon.nosql.keywordsparser.InvalidKeywordException;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.naming.directory.SchemaViolationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class Person {
    public static JsonObject newPerson(String name, int age, String major) {
        JsonObject person = new JsonObject();
        person.addProperty("name", name);
        person.addProperty("age", age);
        person.addProperty("major", major);
        return person;
    }
}

class DefaultDocumentsCollectionTest {
    private final Path testDirectory = Path.of("./test");

    private final GsonDocumentSchema documentSchema = new GsonDocumentSchema(
            "{name: \"string;required\", age: \"number;default(18)\", major: \"string;required\"}"
    );

    DefaultDocumentsCollectionTest() throws InvalidKeywordException {
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
        DocumentsCollection<GsonDocument> collection =
                new DefaultDocumentsCollection<>(documentSchema, parser, testDirectory);
        GsonDocument khalid = new GsonDocument(Person.newPerson("Khalid", 22, "CPE"));
        GsonDocument hamza = new GsonDocument(Person.newPerson("Hamza", 22, "CPE"));
        GsonDocument john = new GsonDocument(Person.newPerson("John", 42, "CIS"));
        collection.put(khalid);
        collection.put(hamza);
        collection.put(john);
        GsonDocument matchKhalid = (GsonDocument) khalid.withField("_matchID", new JsonPrimitive(true));
        assertTrue(List.of(khalid).containsAll(collection.get(matchKhalid)));
        JsonObject matchCpeObject = new JsonObject();
        matchCpeObject.addProperty("major", "CPE");
        GsonDocument matchCpe = GsonMatchDocument.newGsonMatchDocument(matchCpeObject, false);
        Collection<GsonDocument> cpeStudents = collection.get(matchCpe);
        assertTrue(List.of(khalid, hamza).containsAll(cpeStudents));
    }

    @Test
    void remove() throws IOException, SchemaViolationException {
        GsonDocumentParser parser = new GsonDocumentParser();
        DocumentsCollection<GsonDocument> collection = new DefaultDocumentsCollection<>(documentSchema, parser, testDirectory);

    }

    @Test
    void readAll() throws IOException, SchemaViolationException {
        GsonDocumentParser parser = new GsonDocumentParser();
        DocumentsCollection<GsonDocument> collection = new DefaultDocumentsCollection<>(documentSchema, parser, testDirectory);

    }
}
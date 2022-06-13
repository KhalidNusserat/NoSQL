package com.atypon.nosql.collection;

import com.atypon.nosql.document.DocumentGenerator;
import com.atypon.nosql.document.ObjectIdGenerator;
import com.atypon.nosql.document.RandomObjectIdGenerator;
import com.atypon.nosql.gsondocument.FieldsDoNotMatchException;
import com.atypon.nosql.gsondocument.GsonDocument;
import com.atypon.nosql.gsondocument.GsonDocumentGenerator;
import com.atypon.nosql.io.DefaultIOEngine;
import com.atypon.nosql.utils.ExtraFileUtils;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Person {
    private static final ObjectIdGenerator idGenerator = new RandomObjectIdGenerator();

    private static final GsonDocumentGenerator documentGenerator = new GsonDocumentGenerator(idGenerator);

    public static GsonDocument newPerson(String name, int age, String major) {
        String src = String.format("{name: %s, age: %d, major: %s}", name, age, major);
        return documentGenerator.appendId(documentGenerator.createFromString(src));
    }
}

public abstract class DocumentsCollectionTest<T extends DocumentsCollection<GsonDocument>> {
    protected final Path testDirectory = Path.of("./test");

    protected final GsonDocument khalid = Person.newPerson("Khalid", 22, "CPE");

    protected final GsonDocument hamza = Person.newPerson("Hamza", 22, "CPE");

    protected final GsonDocument john = Person.newPerson("John", 42, "CIS");

    protected final DefaultIOEngine ioEngine = new DefaultIOEngine();

    protected final ObjectIdGenerator idGenerator = new RandomObjectIdGenerator();

    protected final DocumentGenerator<GsonDocument> documentGenerator = new GsonDocumentGenerator(idGenerator);

    public abstract T create();

    @BeforeEach
    void setUp() throws IOException {
        Files.createDirectories(testDirectory);
    }

    @AfterEach
    void tearDown() {
        ExtraFileUtils.deleteDirectory(testDirectory);
    }

    @Test
    void putAndGet() throws IOException, FieldsDoNotMatchException {
        T collection = create();
        collection.addDocument(khalid);
        collection.addDocument(hamza);
        collection.addDocument(john);
        assertEquals(List.of(khalid), collection.getAllThatMatches(GsonDocument.fromString("{name: \"Khalid\"}")));
        JsonObject matchCpeObject = new JsonObject();
        matchCpeObject.addProperty("major", "CPE");
        GsonDocument matchCpe = GsonDocument.fromJsonObject(matchCpeObject);
        assertEquals(Set.of(khalid, hamza), Set.copyOf(collection.getAllThatMatches(matchCpe)));
    }

    @Test
    void remove() throws IOException, InterruptedException, FieldsDoNotMatchException {
        T collection = create();
        collection.addDocument(khalid);
        collection.addDocument(hamza);
        collection.addDocument(john);
        JsonObject matchCpeStudents = new JsonObject();
        matchCpeStudents.addProperty("major", "CPE");
        collection.deleteAllThatMatches(GsonDocument.fromJsonObject(matchCpeStudents));
        Thread.sleep(100);
        assertEquals(Set.of(john), Set.copyOf(collection.getAll()));
        assertEquals(0, ExtraFileUtils.countFiles(testDirectory, 1));
    }

    @Test
    void getAll() throws IOException {
        T collection = create();
        collection.addDocument(khalid);
        collection.addDocument(hamza);
        collection.addDocument(john);
        assertTrue(List.of(khalid, hamza, john).containsAll(collection.getAll()));
    }
}

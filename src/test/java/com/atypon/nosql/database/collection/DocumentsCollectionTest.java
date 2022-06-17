package com.atypon.nosql.database.collection;

import com.atypon.nosql.database.document.Document;
import com.atypon.nosql.database.document.DocumentFactory;
import com.atypon.nosql.database.document.ObjectIdGenerator;
import com.atypon.nosql.database.document.RandomObjectIdGenerator;
import com.atypon.nosql.database.gsondocument.FieldsDoNotMatchException;
import com.atypon.nosql.database.gsondocument.GsonDocument;
import com.atypon.nosql.database.gsondocument.GsonDocumentFactory;
import com.atypon.nosql.database.io.BasicIOEngine;
import com.atypon.nosql.database.utils.FileUtils;
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

    private static final GsonDocumentFactory documentGenerator = new GsonDocumentFactory(idGenerator);

    public static GsonDocument newPerson(String name, int age, String major) {
        String src = String.format("{name: %s, age: %d, major: %s}", name, age, major);
        return documentGenerator.appendId(documentGenerator.createFromString(src));
    }
}

public abstract class DocumentsCollectionTest<T extends DocumentsCollection> {
    protected final Path testDirectory = Path.of("./test");

    protected final GsonDocument khalid = Person.newPerson("Khalid", 22, "CPE");

    protected final GsonDocument hamza = Person.newPerson("Hamza", 22, "CPE");

    protected final GsonDocument john = Person.newPerson("John", 42, "CIS");

    protected final BasicIOEngine ioEngine;

    protected final ObjectIdGenerator idGenerator = new RandomObjectIdGenerator();

    protected final DocumentFactory documentFactory = new GsonDocumentFactory(idGenerator);

    protected DocumentsCollectionTest() {
        ioEngine = new BasicIOEngine(documentFactory);
    }

    public abstract T create();

    @BeforeEach
    void setUp() throws IOException {
        Files.createDirectories(testDirectory);
    }

    @AfterEach
    void tearDown() {
        FileUtils.deleteDirectory(testDirectory);
    }

    @Test
    void putAndGet() throws FieldsDoNotMatchException {
        T collection = create();
        collection.addDocument(khalid);
        collection.addDocument(hamza);
        collection.addDocument(john);
        assertEquals(List.of(khalid), collection.getAllThatMatch(GsonDocument.fromString("{name: \"Khalid\"}")));
        JsonObject matchCpeObject = new JsonObject();
        matchCpeObject.addProperty("major", "CPE");
        GsonDocument matchCpe = GsonDocument.fromJsonObject(matchCpeObject);
        assertEquals(Set.of(khalid, hamza), Set.copyOf(collection.getAllThatMatch(matchCpe)));
    }

    @Test
    void remove() throws InterruptedException, FieldsDoNotMatchException {
        T collection = create();
        collection.addDocument(khalid);
        collection.addDocument(hamza);
        collection.addDocument(john);
        JsonObject matchCpeStudents = new JsonObject();
        matchCpeStudents.addProperty("major", "CPE");
        collection.removeAllThatMatch(GsonDocument.fromJsonObject(matchCpeStudents));
        Thread.sleep(100);
        assertEquals(Set.of(john), Set.copyOf(collection.getAll()));
    }

    @Test
    void getAll() {
        T collection = create();
        collection.addDocument(khalid);
        collection.addDocument(hamza);
        collection.addDocument(john);
        List<Document> result = collection.getAll();
        assertTrue(List.of(khalid, hamza, john).containsAll(result));
    }
}

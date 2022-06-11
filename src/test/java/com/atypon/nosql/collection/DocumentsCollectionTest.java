package com.atypon.nosql.collection;

import com.atypon.nosql.gsondocument.GsonDocument;
import com.atypon.nosql.gsondocument.GsonDocumentsIO;
import com.atypon.nosql.utils.ExtraFileUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.naming.directory.SchemaViolationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Person {
    public static JsonObject newPerson(String name, int age, String major) {
        JsonObject person = new JsonObject();
        person.addProperty("name", name);
        person.addProperty("age", age);
        person.addProperty("major", major);
        return person;
    }
}

public abstract class DocumentsCollectionTest<T extends DocumentsCollection<GsonDocument>> {
    protected final Path testDirectory = Path.of("./test");

    protected final GsonDocument khalid = new GsonDocument(Person.newPerson("Khalid", 22, "CPE"));

    protected final GsonDocument hamza = new GsonDocument(Person.newPerson("Hamza", 22, "CPE"));

    protected final GsonDocument john = new GsonDocument(Person.newPerson("John", 42, "CIS"));

    protected final Gson gson = new Gson();

    protected final GsonDocumentsIO documentsIO = new GsonDocumentsIO(gson);

    public abstract T create();

    private void deleteDirectory(Path directory) {
        try {
            Files.walk(directory)
                    .forEach(path -> {
                        if (!path.equals(directory) && Files.isDirectory(path)) {
                            deleteDirectory(path);
                        } else if (Files.isRegularFile(path)) {
                            try {
                                Files.delete(path);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
            Files.delete(directory);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    @BeforeEach
    void setUp() throws IOException {
        Files.createDirectories(testDirectory);
    }

    @AfterEach
    void tearDown() {
        deleteDirectory(testDirectory);
    }

    @Test
    void putAndGet() throws IOException, SchemaViolationException {
        T collection = create();
        collection.put(khalid);
        collection.put(hamza);
        collection.put(john);
        assertEquals(List.of(khalid), collection.getAllThatMatches((GsonDocument) khalid.matchID()));
        JsonObject matchCpeObject = new JsonObject();
        matchCpeObject.addProperty("major", "CPE");
        GsonDocument matchCpe = GsonDocument.fromJsonObject(matchCpeObject);
        assertEquals(Set.of(khalid, hamza), Set.copyOf(collection.getAllThatMatches(matchCpe)));
    }

    @Test
    void remove() throws IOException, SchemaViolationException, InterruptedException {
        T collection = create();
        collection.put(khalid);
        collection.put(hamza);
        collection.put(john);
        JsonObject matchCpeStudents = new JsonObject();
        matchCpeStudents.addProperty("major", "CPE");
        collection.deleteAllThatMatches(GsonDocument.fromJsonObject(matchCpeStudents));
        Thread.sleep(100);
        assertEquals(Set.of(john), Set.copyOf(collection.getAll()));
        assertEquals(1, ExtraFileUtils.countFiles(testDirectory, 1));
    }

    @Test
    void getAll() throws IOException, SchemaViolationException {
        T collection = create();
        collection.put(khalid);
        collection.put(hamza);
        collection.put(john);
        assertTrue(List.of(khalid, hamza, john).containsAll(collection.getAll()));
    }
}

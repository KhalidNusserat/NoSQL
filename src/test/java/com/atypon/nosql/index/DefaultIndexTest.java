package com.atypon.nosql.index;

import com.atypon.nosql.document.DocumentGenerator;
import com.atypon.nosql.document.ObjectIdGenerator;
import com.atypon.nosql.document.RandomObjectIdGenerator;
import com.atypon.nosql.gsondocument.FieldsDoNotMatchException;
import com.atypon.nosql.gsondocument.GsonDocument;
import com.atypon.nosql.gsondocument.GsonDocumentGenerator;
import com.atypon.nosql.io.GsonIOEngine;
import com.atypon.nosql.io.IOEngine;
import com.google.gson.Gson;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultIndexTest {
    private final Path testDirectory = Path.of("./test");

    private final Gson gson = new Gson();

    private final IOEngine ioEngine = new GsonIOEngine(gson);

    private final ObjectIdGenerator idGenerator = new RandomObjectIdGenerator();

    private final DocumentGenerator<GsonDocument> documentGenerator = new GsonDocumentGenerator(idGenerator);

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
    public void putAndGet() throws FieldsDoNotMatchException {
        JsonObject matchUniversity = new JsonObject();
        matchUniversity.add("name", JsonNull.INSTANCE);
        GsonDocument matchNameAndUniversity = GsonDocument.fromString(
                "{name: null, university: {name: null}}"
        );
        DefaultIndex<GsonDocument> fieldIndex = new DefaultIndex<>(
                matchNameAndUniversity,
                Path.of("."),
                ioEngine,
                documentGenerator
        );
        JsonObject khalidObject = new JsonObject();
        khalidObject.addProperty("name", "Khalid");
        JsonObject yarmouk = new JsonObject();
        yarmouk.addProperty("name", "Yarmouk");
        khalidObject.add("university", yarmouk);
        JsonObject hamzaObject = new JsonObject();
        hamzaObject.addProperty("name", "Hamza");
        hamzaObject.add("university", yarmouk);
        JsonObject ahmadObject = new JsonObject();
        ahmadObject.addProperty("name", "Ahmad");
        JsonObject just = new JsonObject();
        just.addProperty("name", "JUST");
        ahmadObject.add("university", just);
        JsonObject otherKhalidObject = new JsonObject();
        otherKhalidObject.addProperty("name", "Khalid");
        otherKhalidObject.add("university", yarmouk);
        GsonDocument khalid = GsonDocument.fromJsonObject(khalidObject);
        GsonDocument hamza = GsonDocument.fromJsonObject(hamzaObject);
        GsonDocument ahmad = GsonDocument.fromJsonObject(ahmadObject);
        GsonDocument otherKhalid = GsonDocument.fromJsonObject(otherKhalidObject);
        Path khalidPath = Path.of("./khalid");
        Path hamzaPath = Path.of("./hamza");
        Path otherKhalidPath = Path.of("./otherKhalid");
        Path ahmadPath = Path.of("./ahmad");
        fieldIndex.add(khalid, khalidPath);
        fieldIndex.add(hamza, hamzaPath);
        fieldIndex.add(ahmad, ahmadPath);
        fieldIndex.add(otherKhalid, otherKhalidPath);
        assertEquals(Set.of(khalidPath, otherKhalidPath), fieldIndex.get(khalid));
        assertEquals(Set.of(hamzaPath), fieldIndex.get(hamza));
    }
}
package com.atypon.nosql.index;

import com.atypon.nosql.document.DocumentField;
import com.atypon.nosql.gsondocument.GsonDocument;
import com.atypon.nosql.gsondocument.GsonDocumentParser;
import com.atypon.nosql.io.GsonDocumentsIO;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class GsonFieldIndexTest {
    private final Path testDirectory = Path.of("./test");

    private final Gson gson = new Gson();

    private final GsonDocumentsIO documentsIO = new GsonDocumentsIO(new GsonDocumentParser());

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
    public void putAndGet() {
        GsonFieldIndex fieldIndex = new GsonFieldIndex(
                Set.of(
                        DocumentField.of("name"),
                        DocumentField.of("university", "name")
                ),
                testDirectory,
                gson
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
        GsonDocument khalid = GsonDocument.of(khalidObject);
        GsonDocument hamza = GsonDocument.of(hamzaObject);
        GsonDocument ahmad = GsonDocument.of(ahmadObject);
        GsonDocument otherKhalid = GsonDocument.of(otherKhalidObject);
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
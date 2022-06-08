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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IndexedDocumentsCollectionTest {
    private final Path testDirectory = Path.of("./test");

    private final GsonDocumentSchema documentSchema = new GsonDocumentSchema(
            "{name: \"string;required\", age: \"number;default(18)\", major: \"string;required\"}"
    );

    IndexedDocumentsCollectionTest() throws InvalidKeywordException {
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
    void contains() throws IOException, SchemaViolationException {

    }
}
package com.atypon.nosql.gsondocument;

import com.atypon.nosql.document.DocumentField;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GsonDocumentTest {
    @Test
    void getFields() {
        JsonObject person = new JsonObject();
        person.addProperty("name", "");
        person.addProperty("age", 0);
        JsonObject university = new JsonObject();
        university.addProperty("name", "");
        university.addProperty("rating", 0);
        person.add("university", university);
        assertEquals(Set.of(
                DocumentField.of("name"),
                DocumentField.of("age"),
                DocumentField.of("university", "name"),
                DocumentField.of("university", "rating"),
                DocumentField.of("_id")
        ), new GsonDocument(person).getFields());
    }

    @Test
    void getValues() {
        JsonObject person = new JsonObject();
        person.addProperty("name", "Khalid");
        person.addProperty("age", 18);
        JsonObject university = new JsonObject();
        university.addProperty("name", "Yarmouk");
        university.addProperty("rating", 3);
        person.add("university", university);
        assertTrue(
                new GsonDocument(person).getAll().stream()
                        .map(JsonElement::getAsJsonPrimitive)
                        .toList().containsAll(
                                Set.of(
                                        new JsonPrimitive("Khalid"),
                                        new JsonPrimitive("Yarmouk"),
                                        new JsonPrimitive(18),
                                        new JsonPrimitive(3)
                                )
                        )
        );
    }

    @Test
    public void getValuesByFields() {
        JsonObject person = new JsonObject();
        person.addProperty("name", "Khalid");
        person.addProperty("age", 18);
        JsonObject university = new JsonObject();
        university.addProperty("name", "Yarmouk");
        university.addProperty("rating", 3);
        person.add("university", university);
        assertEquals(
                "Yarmouk",
                new GsonDocument(person).get(DocumentField.of("university", "name")).getAsString()
        );
        assertEquals(
                3,
                new GsonDocument(person).get(DocumentField.of("university", "rating")).getAsInt()
        );
    }
}
package com.atypon.nosql.gsondocument;

import com.atypon.nosql.document.DocumentField;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    public void match() {
        JsonObject person = new JsonObject();
        person.addProperty("name", "Khalid");
        GsonDocument matchName = GsonDocument.of(person);
        person.addProperty("age", 18);
        GsonDocument matchNameAndAge = GsonDocument.of(person);
        JsonObject university = new JsonObject();
        university.addProperty("name", "Yarmouk");
        university.addProperty("rating", 3);
        person.add("university", university);
        GsonDocument matchEverything = GsonDocument.of(person);
        GsonDocument khalid = GsonDocument.of(person);
        assertTrue(khalid.matches(matchName));
        assertTrue(khalid.matches(matchEverything));
        assertTrue(khalid.matches(matchNameAndAge));
        assertTrue(khalid.matches(khalid.matchID()));
        JsonObject otherPerson = new JsonObject();
        otherPerson.addProperty("name", "John Doe");
        GsonDocument matchOtherName = GsonDocument.of(otherPerson);
        otherPerson.addProperty("age", "42");
        GsonDocument matchOtherNameAndAge = GsonDocument.of(otherPerson);
        otherPerson.add("university", university);
        GsonDocument matchOtherEverything = GsonDocument.of(otherPerson);
        GsonDocument otherPersonDocument = GsonDocument.of(otherPerson);
        assertFalse(khalid.matches(matchOtherName));
        assertFalse(khalid.matches(matchOtherNameAndAge));
        assertFalse(khalid.matches(matchOtherEverything));
        assertFalse(khalid.matches(otherPersonDocument.matchID()));
    }
}
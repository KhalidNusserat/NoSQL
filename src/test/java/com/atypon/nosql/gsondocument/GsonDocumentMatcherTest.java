package com.atypon.nosql.gsondocument;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
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

class GsonDocumentMatcherTest {
    @Test
    void matches() {
        GsonDocument khalid = new GsonDocument(Person.newPerson("Khalid", 22, "CPE"));
        GsonDocument hamza = new GsonDocument(Person.newPerson("Hamza", 22, "CPE"));
        GsonDocument john = new GsonDocument(Person.newPerson("John", 42, "CIS"));
        JsonObject majorBoundObject = new JsonObject();
        majorBoundObject.addProperty("major", "CPE");
        assertTrue(khalid.matches(GsonMatchDocument.newGsonMatchDocument(majorBoundObject, false)));
        assertTrue(hamza.matches(GsonMatchDocument.newGsonMatchDocument(majorBoundObject, false)));
        assertFalse(john.matches(GsonMatchDocument.newGsonMatchDocument(majorBoundObject, false)));
        assertFalse(khalid.matches(john.matchID()));
        assertFalse(hamza.matches(john.matchID()));
        assertTrue(john.matches(john.matchID()));
    }

    @Test
    void getFields() {
        JsonObject person = new JsonObject();
        person.addProperty("name", "");
        person.addProperty("age", 0);
        JsonObject university = new JsonObject();
        university.addProperty("name", "");
        university.addProperty("rating", 0);
        person.add("university", university);
        var fields = new GsonDocument(person).getFields();
        assertTrue(List.of(
                List.of("name"),
                List.of("age"),
                List.of("university", "name"),
                List.of("university", "rating"),
                List.of("_id")
        ).containsAll(fields));
    }
}
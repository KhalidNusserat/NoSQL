package com.atypon.nosql.gsondocument;

import com.atypon.nosql.document.DocumentField;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.junit.jupiter.api.Test;

import java.util.Set;

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
}
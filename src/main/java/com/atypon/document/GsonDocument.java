package com.atypon.document;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Objects;

public class GsonDocument implements Document<JsonElement> {
    private final JsonObject object;

    public GsonDocument() {
        object = new JsonObject();
    }

    public GsonDocument(GsonDocument other) {
        object = other.object.deepCopy();
    }

    @Override
    public void add(String property, JsonElement jsonElement) {
        object.add(property, jsonElement);
    }

    @Override
    public void addProperty(String property, double value) {
        object.addProperty(property, value);
    }

    @Override
    public void addProperty(String property, String value) {
        object.addProperty(property, value);
    }

    @Override
    public void addProperty(String property, boolean value) {
        object.addProperty(property, value);
    }

    @Override
    public JsonElement get(String property) {
        return object.get(property);
    }

    @Override
    public JsonElement remove(String property) {
        return object.remove(property);
    }

    @Override
    public Document<JsonElement> deepCopy() {
        return new GsonDocument(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GsonDocument that = (GsonDocument) o;
        return object.equals(that.object);
    }

    @Override
    public int hashCode() {
        return Objects.hash(object);
    }

    @Override
    public String toString() {
        return object.toString();
    }
}
package com.atypon.document;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

public class GsonDocument implements Document<JsonElement> {
    private final JsonObject object;

    private final ObjectID objectID = new RandomObjectID();

    public GsonDocument() {
        object = new JsonObject();
        object.addProperty("id", objectID.toString());
    }

    public GsonDocument(GsonDocument other) {
        object = other.object.deepCopy();
        object.addProperty("id", objectID.toString());
    }

    public GsonDocumentBuilder builder() {
        return new GsonDocumentBuilder();
    }

    private void add(String field, JsonElement jsonElement) {
        object.add(field, jsonElement);
    }

    private void addField(String field, BigInteger value) {
        object.addProperty(field, value);
    }

    private void addField(String field, BigDecimal value) {
        object.addProperty(field, value);
    }

    private void addField(String field, String value) {
        object.addProperty(field, value);
    }

    private void addField(String field, boolean value) {
        object.addProperty(field, value);
    }

    private void remove(String field) {
        object.remove(field);
    }

    @Override
    public ObjectID id() {
        return objectID;
    }

    @Override
    public JsonElement get(String field) {
        return object.get(field);
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

    public static class GsonDocumentBuilder {
        private final GsonDocument gsonDocument = new GsonDocument();

        public GsonDocumentBuilder add(String field, JsonElement element) {
            gsonDocument.add(field, element);
            return this;
        }

        public GsonDocumentBuilder addProperty(String field, BigInteger value) {
            gsonDocument.addField(field, value);
            return this;
        }

        public GsonDocumentBuilder addProperty(String field, BigDecimal value) {
            gsonDocument.addField(field, value);
            return this;
        }

        public GsonDocumentBuilder addProperty(String field, String value) {
            gsonDocument.addField(field, value);
            return this;
        }

        public GsonDocumentBuilder addProperty(String field, boolean value) {
            gsonDocument.addField(field, value);
            return this;
        }

        public GsonDocumentBuilder remove(String field) {
            gsonDocument.remove(field);
            return this;
        }

        public GsonDocument create() {
            return gsonDocument;
        }
    }
}

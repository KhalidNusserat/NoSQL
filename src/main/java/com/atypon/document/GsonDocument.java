package com.atypon.document;

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
            gsonDocument.object.add(field, element);
            return this;
        }

        public GsonDocumentBuilder addField(String field, BigInteger value) {
            gsonDocument.object.addProperty(field, value);
            return this;
        }

        public GsonDocumentBuilder addField(String field, BigDecimal value) {
            gsonDocument.object.addProperty(field, value);
            return this;
        }

        public GsonDocumentBuilder addField(String field, String value) {
            gsonDocument.object.addProperty(field, value);
            return this;
        }

        public GsonDocumentBuilder addField(String field, boolean value) {
            gsonDocument.object.addProperty(field, value);
            return this;
        }

        public GsonDocumentBuilder remove(String field) {
            gsonDocument.object.remove(field);
            return this;
        }

        public GsonDocument create() {
            return gsonDocument;
        }
    }
}

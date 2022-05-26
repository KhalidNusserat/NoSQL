package com.atypon.nosql.gsonwrapper;

import com.atypon.nosql.document.Document;
import com.atypon.nosql.document.ObjectID;
import com.atypon.nosql.document.RandomObjectID;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

public class GsonObject implements Document<JsonElement> {
    private final JsonObject object;

    private final ObjectID objectID = new RandomObjectID();

    public GsonObject() {
        object = new JsonObject();
        object.addProperty("_id", objectID.toString());
    }

    public GsonObject(GsonObject other) {
        object = other.object.deepCopy();
        object.addProperty("_id", objectID.toString());
    }

    public static GsonDocumentBuilder builder() {
        return new GsonDocumentBuilder();
    }

    public JsonObject getAsJsonObject() {
        return object;
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
        return new GsonObject(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GsonObject that = (GsonObject) o;
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
        private final GsonObject gsonObject = new GsonObject();

        public GsonDocumentBuilder add(String field, JsonElement element) {
            gsonObject.object.add(field, element);
            return this;
        }

        public GsonDocumentBuilder addField(String field, BigInteger value) {
            gsonObject.object.addProperty(field, value);
            return this;
        }

        public GsonDocumentBuilder addField(String field, BigDecimal value) {
            gsonObject.object.addProperty(field, value);
            return this;
        }

        public GsonDocumentBuilder addField(String field, String value) {
            gsonObject.object.addProperty(field, value);
            return this;
        }

        public GsonDocumentBuilder addField(String field, boolean value) {
            gsonObject.object.addProperty(field, value);
            return this;
        }

        public GsonDocumentBuilder remove(String field) {
            gsonObject.object.remove(field);
            return this;
        }

        public GsonObject create() {
            return gsonObject;
        }
    }
}

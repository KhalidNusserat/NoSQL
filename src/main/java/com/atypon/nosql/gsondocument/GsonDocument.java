package com.atypon.nosql.gsondocument;

import com.atypon.nosql.document.Document;
import com.atypon.nosql.document.ObjectID;
import com.atypon.nosql.document.RandomObjectID;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class GsonDocument implements Document<JsonElement> {
    private final JsonObject object;

    private final ObjectID objectID = new RandomObjectID();

    public GsonDocument() {
        object = new JsonObject();
        object.addProperty("_id", objectID.toString());
    }

    public GsonDocument(JsonObject object) {
        this.object = object.deepCopy();
        object.addProperty("_id", objectID.toString());
    }

    public GsonDocument(JsonElement element) {
        Preconditions.checkState(
                element.isJsonObject(),
                "Expected a JsonObject, instead got: %s", element
        );
        object = element.getAsJsonObject();
        object.addProperty("_id", objectID.toString());
    }

    public GsonDocument(GsonDocument other) {
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

        public GsonDocumentBuilder remove(String field) {
            gsonDocument.object.remove(field);
            return this;
        }

        public boolean containsKey(String field) {
            return gsonDocument.getAsJsonObject().has(field);
        }

        public GsonDocument create() {
            return gsonDocument;
        }
    }
}

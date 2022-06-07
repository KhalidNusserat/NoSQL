package com.atypon.nosql.gsondocument;

import com.atypon.nosql.document.Document;
import com.atypon.nosql.document.ObjectIDGenerator;
import com.atypon.nosql.document.RandomObjectIDGenerator;
import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.Objects;

public class GsonDocument implements Document<JsonElement> {
    final JsonObject object;

    private final ObjectIDGenerator objectIDGenerator = new RandomObjectIDGenerator();

    private GsonDocument(GsonDocument other) {
        object = other.object.deepCopy();
        object.addProperty("_id", objectIDGenerator.getNewId());
    }

    public GsonDocument() {
        object = new JsonObject();
        object.addProperty("_id", objectIDGenerator.getNewId());
    }

    public GsonDocument(JsonObject object) {
        this.object = object.deepCopy();
        this.object.addProperty("_id", objectIDGenerator.getNewId());
    }

    public static GsonDocumentBuilder builder() {
        return new GsonDocumentBuilder();
    }

    public JsonObject getAsJsonObject() {
        return object;
    }

    @Override
    public String id() {
        return get("_id").getAsString();
    }

    @Override
    public JsonElement get(String field) {
        return object.get(field);
    }

    @Override
    public boolean matches(Document<JsonElement> bound) {
        return GsonDocumentMatcher.matches(this, (GsonDocument) bound);
    }

    @Override
    public Document<JsonElement> withField(String field, JsonElement element) {
        GsonDocument document = new GsonDocument(this);
        document.object.add(field, element);
        return document;
    }

    @Override
    public Document<JsonElement> withoutField(String field) {
        GsonDocument document = new GsonDocument(this);
        document.object.remove(field);
        return document;
    }

    @Override
    public Document<JsonElement> matchID() {
        GsonDocument document = (GsonDocument) new GsonDocument()
                .withField("_matchID", new JsonPrimitive(true));
        document.object.addProperty("_id", id());
        return document;
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

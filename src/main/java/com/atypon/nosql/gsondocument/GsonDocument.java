package com.atypon.nosql.gsondocument;

import com.atypon.nosql.document.Document;
import com.atypon.nosql.document.DocumentField;
import com.atypon.nosql.document.ObjectIDGenerator;
import com.atypon.nosql.document.RandomObjectIDGenerator;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.*;
import java.util.stream.Collectors;

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

    public static GsonDocument of(JsonObject object) {
        return new GsonDocument(object);
    }

    private Set<DocumentField> getFields(JsonObject object, DocumentField field) {
        Set<DocumentField> result = new HashSet<>();
        for (var entry : object.entrySet()) {
            DocumentField currentField = field.with(entry.getKey());
            if (entry.getValue().isJsonPrimitive() || entry.getValue().isJsonNull() || entry.getValue().isJsonArray()) {
                result.add(currentField);
            } else {
                result.addAll(getFields(entry.getValue().getAsJsonObject(), currentField));
            }
        }
        return result;
    }

    private Set<JsonElement> get(JsonObject object) {
        Set<JsonElement> result = new HashSet<>();
        for (var entry : object.entrySet()) {
            if (entry.getValue().isJsonPrimitive() || entry.getValue().isJsonNull() || entry.getValue().isJsonArray()) {
                result.add(entry.getValue());
            } else {
                result.addAll(get(entry.getValue().getAsJsonObject()));
            }
        }
        return result;
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
        return new GsonDocument().withField("_matchID", new JsonPrimitive(id()));
    }

    @Override
    public Set<DocumentField> getFields() {
        return getFields(object, DocumentField.of());
    }

    @Override
    public Set<JsonElement> getAll() {
        return get(object);
    }

    @Override
    public JsonElement get(DocumentField field) {
        JsonObject currentObject = object;
        for (Iterator<String> iterator = field.iterator(); iterator.hasNext(); ) {
            JsonElement element = currentObject.get(iterator.next());
            if (element == null) {
                throw new IllegalArgumentException("Invalid field: " + field + " for the document: " + this);
            }
            if (iterator.hasNext()) {
                currentObject = element.getAsJsonObject();
            } else {
                return element;
            }
        }
        return null;
    }

    @Override
    public Set<JsonElement> getAll(Set<DocumentField> fields) {
        return fields.stream()
                .map(this::get)
                .collect(Collectors.toSet());
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

package com.atypon.nosql.database.gsondocument;

import com.atypon.nosql.database.document.Document;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;

public class GsonDocument implements Document {
    private final static Gson gson = new Gson();

    private final static Type mapType = new TypeToken<Map<String, Object>>() {
    }.getType();

    final JsonObject object;

    private GsonDocument(JsonObject object) {
        this.object = object.deepCopy();
    }

    public static GsonDocument fromJsonObject(JsonObject object) {
        return new GsonDocument(object);
    }

    public static GsonDocument fromString(String src) {
        return GsonDocument.fromJsonObject(gson.fromJson(src, JsonObject.class));
    }

    public static GsonDocument fromMap(Map<String, Object> map) {
        return GsonDocument.fromJsonObject(gson.toJsonTree(map).getAsJsonObject());
    }

    @Override
    public boolean subsetOf(Document matchDocument) {
        return firstSubsetOfSecond(object, ((GsonDocument) matchDocument).object);
    }

    private boolean firstSubsetOfSecond(JsonElement first, JsonElement second) {
        if (first.getClass() != second.getClass()) {
            return false;
        }
        if (first.isJsonArray() || first.isJsonPrimitive() || first.isJsonNull()) {
            return first.equals(second);
        }
        for (var entry : first.getAsJsonObject().entrySet()) {
            String field = entry.getKey();
            JsonElement element = entry.getValue();
            if (!second.getAsJsonObject().has(field)) {
                return false;
            }
            if (!firstSubsetOfSecond(element, second.getAsJsonObject().get(field))) {
                return false;
            }
        }
        return true;
    }

    private JsonElement valuesToMatch(JsonElement fieldsSource, JsonElement valuesSource) {
        if (fieldsSource.isJsonArray() || fieldsSource.isJsonPrimitive() || fieldsSource.isJsonNull()) {
            return valuesSource;
        }
        if (!valuesSource.isJsonObject()) {
            throw new FieldsDoNotMatchException();
        }
        JsonObject result = new JsonObject();
        for (var entry : fieldsSource.getAsJsonObject().entrySet()) {
            String field = entry.getKey();
            JsonElement element = entry.getValue();
            if (!valuesSource.getAsJsonObject().has(field)) {
                throw new FieldsDoNotMatchException();
            }
            JsonElement matchedFields = valuesToMatch(element, valuesSource.getAsJsonObject().get(field));
            result.add(field, matchedFields);
            return result;
        }
        throw new IllegalStateException();
    }

    @Override
    public Document getValuesToMatch(Document otherDocument) {
        try {
            JsonObject otherDocumentObject = ((GsonDocument) otherDocument).object;
            JsonObject matchedObject = valuesToMatch(otherDocumentObject, object).getAsJsonObject();
            return new GsonDocument(matchedObject);
        } catch (FieldsDoNotMatchException e) {
            throw new FieldsDoNotMatchException(this, otherDocument);
        }
    }

    private JsonObject getCriteriaObject(JsonObject object) {
        JsonObject result = new JsonObject();
        for (var entry : object.entrySet()) {
            String field = entry.getKey();
            JsonElement element = entry.getValue();
            if (element.isJsonNull() || element.isJsonPrimitive() || element.isJsonArray()) {
                result.add(field, JsonNull.INSTANCE);
            } else {
                result.add(field, getCriteriaObject(element.getAsJsonObject()));
            }
        }
        return result;
    }

    @Override
    public Document getFields() {
        return GsonDocument.fromJsonObject(getCriteriaObject(object));
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

    @Override
    public Map<String, Object> getAsMap() {
        return gson.fromJson(object, mapType);
    }
}

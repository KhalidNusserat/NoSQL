package com.atypon.nosql.gsondocument;

import com.atypon.nosql.document.Document;
import com.atypon.nosql.document.FieldsDoNotMatchException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.reflect.TypeToken;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;

public class GsonDocument extends Document {

    private final static Gson gson = new GsonBuilder()
            .serializeNulls()
            .create();

    private final static Type mapType = new TypeToken<Map<String, Object>>() {
    }.getType();
    final JsonObject object;

    private GsonDocument(JsonObject object) {
        this.object = object.deepCopy();
    }

    public static GsonDocument fromJson(String src) {
        JsonObject object = JsonParser.parseString(src).getAsJsonObject();
        return new GsonDocument(handleNumbers(object).getAsJsonObject());
    }

    public static GsonDocument fromMap(Map<String, Object> map) {
        JsonObject jsonObject = gson.toJsonTree(map).getAsJsonObject();
        return new GsonDocument(handleNumbers(jsonObject).getAsJsonObject());
    }

    public static GsonDocument fromObject(Object object) {
        JsonObject jsonObject = gson.toJsonTree(object).getAsJsonObject();
        return new GsonDocument(handleNumbers(jsonObject).getAsJsonObject());
    }

    private static JsonElement handleNumbers(JsonElement element) {
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
            return new JsonPrimitive(element.getAsBigDecimal());
        } else if (element.isJsonPrimitive() || element.isJsonNull()) {
            return element;
        } else if (element.isJsonArray()) {
            JsonArray array = new JsonArray();
            for (JsonElement arrayElement : element.getAsJsonArray()) {
                array.add(handleNumbers(arrayElement));
            }
            return array;
        } else {
            JsonObject result = new JsonObject();
            for (var entry : element.getAsJsonObject().entrySet()) {
                String field = entry.getKey();
                JsonElement value = entry.getValue();
                result.add(field, handleNumbers(value));
            }
            return result;
        }
    }

    @Override
    public boolean subsetOf(Document otherDocument) {
        return firstSubsetOfSecond(object, ((GsonDocument) otherDocument).object);
    }

    private boolean firstSubsetOfSecond(JsonElement first, JsonElement second) {
        if (first.getClass() != second.getClass()) {
            return false;
        } else if (first.isJsonArray() || first.isJsonPrimitive() || first.isJsonNull()) {
            return first.equals(second);
        }
        for (var entry : first.getAsJsonObject().entrySet()) {
            String field = entry.getKey();
            JsonElement element = entry.getValue();
            if (!second.getAsJsonObject().has(field)) {
                return false;
            } else if (!firstSubsetOfSecond(element, second.getAsJsonObject().get(field))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Document getValues(Document fields) {
        JsonObject otherDocumentObject = ((GsonDocument) fields).object;
        JsonObject matchedObject = valuesToMatch(otherDocumentObject, object).getAsJsonObject();
        return new GsonDocument(matchedObject);
    }

    private JsonElement valuesToMatch(JsonElement fieldsSource, JsonElement valuesSource) {
        if (fieldsSource.isJsonArray() || fieldsSource.isJsonPrimitive() || fieldsSource.isJsonNull()) {
            return valuesSource;
        } else if (!valuesSource.isJsonObject()) {
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
    public Document getFields() {
        return new GsonDocument(getCriteriaObject(object));
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
    public Document overrideFields(Document newFieldsValues) {
        JsonObject otherObject = ((GsonDocument) newFieldsValues).object;
        JsonObject newObject = mergeObjects(object, otherObject);
        return new GsonDocument(newObject);
    }

    private JsonObject mergeObjects(JsonObject firstObject, JsonObject secondObject) {
        JsonObject result = firstObject.deepCopy();
        for (var entry : secondObject.entrySet()) {
            String field = entry.getKey();
            JsonElement element = entry.getValue();
            if (element.isJsonObject()) {
                JsonObject object = element.getAsJsonObject();
                result.add(field, mergeObjects(firstObject.get(field).getAsJsonObject(), object));
            } else {
                result.add(field, element);
            }
        }
        return result;
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
        return object.hashCode();
    }

    @Override
    public String toString() {
        return object.toString();
    }

    @Override
    public Map<String, Object> toMap() {
        return gson.fromJson(object, mapType);
    }

    @Override
    public <T> T toObject(Class<T> classOfObject) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return mapper.readValue(toString(), classOfObject);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Document withId() {
        JsonObject objectWithId = object.deepCopy();
        objectWithId.addProperty("_id", idGenerator.newId(object));
        return new GsonDocument(objectWithId);
    }
}

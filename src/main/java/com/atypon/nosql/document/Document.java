package com.atypon.nosql.document;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.reflect.TypeToken;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class Document {

    private final static Gson gson = new GsonBuilder()
            .serializeNulls()
            .create();

    private final static Type mapType = new TypeToken<Map<String, Object>>() {
    }.getType();
    final JsonObject object;

    private Document(JsonObject object) {
        this.object = object.deepCopy();
    }

    public static Document fromJson(String src) {
        JsonObject object = JsonParser.parseString(src).getAsJsonObject();
        return new Document(handleNumbers(object).getAsJsonObject());
    }

    public static Document fromMap(Map<String, Object> map) {
        JsonObject jsonObject = gson.toJsonTree(map).getAsJsonObject();
        return new Document(handleNumbers(jsonObject).getAsJsonObject());
    }

    public static Document fromObject(Object object) {
        JsonObject jsonObject = gson.toJsonTree(object).getAsJsonObject();
        return new Document(handleNumbers(jsonObject).getAsJsonObject());
    }

    public static Document of(Object... elements) {
        Map<String, Object> result = new HashMap<>();
        for (int i = 0; i < elements.length - 1; i += 2) {
            if (elements[i] instanceof String field) {
                result.put(field, elements[i + 1]);
            } else {
                throw new IllegalArgumentException("Field must be a string, instead got: " + elements[i]);
            }
        }
        return fromMap(result);
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

    public boolean subsetOf(Document otherDocument) {
        return firstSubsetOfSecond(object, otherDocument.object);
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

    public Document getValues(Document fields) {
        JsonObject matchedObject = valuesToMatch(fields.object, object).getAsJsonObject();
        return new Document(matchedObject);
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

    public Document getFields() {
        return new Document(getCriteriaObject(object));
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

    public Document overrideFields(Document newFieldsValues) {
        JsonObject newObject = mergeObjects(object, newFieldsValues.object);
        return new Document(newObject);
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

    public Map<String, Object> toMap() {
        return gson.fromJson(object, mapType);
    }

    public <T> T toObject(Class<T> classOfObject) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return mapper.readValue(toString(), classOfObject);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public Document withId(String id) {
        JsonObject objectWithId = object.deepCopy();
        objectWithId.addProperty("_id", id);
        return new Document(objectWithId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Document that = (Document) o;
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
}

package com.atypon.nosql.gsondocument;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class GsonDocumentMatcher {
    private static boolean matchPrimitive(JsonPrimitive primitive, JsonPrimitive bound) {
        return primitive.equals(bound);
    }

    private static boolean matches(JsonElement element, JsonElement bound) {
        if (!element.getClass().equals(bound.getClass())) {
            return false;
        }
        if (element.isJsonPrimitive()) {
            return matchPrimitive(element.getAsJsonPrimitive(), bound.getAsJsonPrimitive());
        } else if (element.isJsonObject()) {
            return matchObject(element.getAsJsonObject(), bound.getAsJsonObject());
        } else {
            throw new IllegalArgumentException(
                    "Expected either a primitive or an object, instead recieved: " + element
            );
        }
    }

    public static boolean matchObject(JsonObject object, JsonObject bound) {
        Set<Map.Entry<String, JsonElement>> documentFields = object.entrySet();
        Set<Map.Entry<String, JsonElement>> boundFields = bound.entrySet();
        for (var entry : boundFields) {
            Optional<Map.Entry<String, JsonElement>> fieldEntry = documentFields.stream()
                    .filter(e -> e.getKey().equals(entry.getKey()))
                    .findAny();
            if (fieldEntry.isPresent() && !matches(entry.getValue(), fieldEntry.get().getValue())) {
                return false;
            }
        }
        return true;
    }
}

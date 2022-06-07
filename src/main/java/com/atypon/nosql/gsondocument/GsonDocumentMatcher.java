package com.atypon.nosql.gsondocument;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.Map;
import java.util.Optional;

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

    private static boolean matchObject(JsonObject object, JsonObject bound) {
        for (var entry : bound.entrySet()) {
            Optional<Map.Entry<String, JsonElement>> fieldEntry = object.entrySet().stream()
                    .filter(e -> e.getKey().equals(entry.getKey()))
                    .findAny();
            if (fieldEntry.isPresent() && !matches(entry.getValue(), fieldEntry.get().getValue())) {
                return false;
            }
        }
        return true;
    }

    public static boolean match(GsonDocument document, GsonDocument matchDocument) {
        Preconditions.checkState(
                matchDocument.object.has("_matchID"),
                "Match documents must have the field \"_matchID\""
        );
        boolean matchID = matchDocument.object.get("_matchID").getAsBoolean();
        for (var entry : matchDocument.object.entrySet()) {
            if (entry.getKey().equals("_matchID")) {
                continue;
            }
            if (entry.getKey().equals("_id") && !matchID) {
                continue;
            }
            Optional<Map.Entry<String, JsonElement>> fieldEntry = document.object.entrySet().stream()
                    .filter(e -> e.getKey().equals(entry.getKey()))
                    .findAny();
            if (fieldEntry.isPresent()) {
                if (!matches(entry.getValue(), fieldEntry.get().getValue())) {
                    return false;
                }
            } else {
                throw new IllegalArgumentException("Invalid field specified: " + entry.getKey());
            }
        }
        return true;
    }
}

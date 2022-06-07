package com.atypon.nosql.schema.gson;

import com.atypon.nosql.gsondocument.GsonElementSchema;
import com.atypon.nosql.gsondocument.GsonElementSchemaParser;
import com.atypon.nosql.keywordsparser.InvalidKeywordException;
import com.atypon.nosql.keywordsparser.KeywordsParser;
import com.atypon.nosql.keywordsparser.SimpleKeywordsParser;
import com.google.gson.*;
import org.junit.jupiter.api.Test;

import javax.naming.directory.SchemaViolationException;

import static org.junit.jupiter.api.Assertions.assertTrue;

class GsonElementSchemaParserTest {
    private boolean compareGsonDocuments(JsonElement first, JsonElement second) {
        if (first.getClass() != second.getClass()) {
            return false;
        }
        if (first.isJsonNull() && second.isJsonNull()) {
            return true;
        } else if (first.isJsonNull() || second.isJsonNull()) {
            return false;
        } else if (first.isJsonPrimitive()) {
            return first.equals(second);
        } else if (first.isJsonArray()) {
            JsonArray firstArray = first.getAsJsonArray();
            JsonArray secondArray = second.getAsJsonArray();
            if (firstArray.size() != secondArray.size()) {
                return false;
            }
            for (int i = 0; i < firstArray.size(); i++) {
                if (!compareGsonDocuments(firstArray.get(i), secondArray.get(i))) {
                    return false;
                }
            }
        } else {
            JsonObject firstObject = first.getAsJsonObject();
            JsonObject secondObject = second.getAsJsonObject();
            for (var field : firstObject.entrySet()) {
                if (field.getKey().equals("_id")) {
                    continue;
                }
                if (!secondObject.has(field.getKey())) {
                    return false;
                }
                if (!compareGsonDocuments(field.getValue(), secondObject.get(field.getKey()))) {
                    return false;
                }
            }
        }
        return true;
    }

    @Test
    void parse() throws InvalidKeywordException, SchemaViolationException {
        String schema = "{name: \"string;required\", age: \"number;default(18)\"," +
                "grades: [{course: \"string;required\", grade: \"number;required\"}]," +
                "extra: \"string;nullable\"}";
        Gson gson = new Gson();
        KeywordsParser keywordsParser = new SimpleKeywordsParser();
        GsonElementSchema gsonSchema = new GsonElementSchemaParser(keywordsParser).parse(gson.fromJson(schema, JsonObject.class));
        String objectJson = "{name: \"Khalid\", grades: [{course: \"CPE231\", grade: 98}], extra: null}";
        JsonObject object = gsonSchema.validate(gson.fromJson(objectJson, JsonObject.class)).getAsJsonObject();
        JsonObject expected = new JsonObject();
        expected.addProperty("name", "Khalid");
        expected.addProperty("age", 18);
        expected.add("extra", JsonNull.INSTANCE);
        JsonArray grades = new JsonArray();
        JsonObject course = new JsonObject();
        course.addProperty("course", "CPE231");
        course.addProperty("grade", 98);
        grades.add(course);
        expected.add("grades", grades);
        assertTrue(compareGsonDocuments(object, expected) && compareGsonDocuments(expected, object));
    }
}
package com.atypon.nosql.schema.gson;

import com.atypon.nosql.keywordsparser.InvalidKeywordException;
import com.atypon.nosql.keywordsparser.Keyword;
import com.atypon.nosql.keywordsparser.KeywordsParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GsonSchemaElementParser {
    private final KeywordsParser keywordsParser;

    public GsonSchemaElementParser(KeywordsParser keywordsParser) {
        this.keywordsParser = keywordsParser;
    }

    private GsonPrimitiveElementSchema<?> parsePrimitive(JsonPrimitive jsonPrimitive) throws InvalidKeywordException {
        if (jsonPrimitive.isString()) {
            String keywordsString = jsonPrimitive.getAsString();
            List<Keyword> keywords = keywordsParser.parse(keywordsString);
            boolean required = keywords.contains(Keyword.fromString("required"));
            boolean nullable = keywords.contains(Keyword.fromString("nullable"));
            Optional<Keyword> defaultKeyword = keywords.stream()
                    .filter(keyword -> keyword.getName().equals("default"))
                    .findFirst();
            if (keywords.contains(Keyword.fromString("number"))) {
                return defaultKeyword.map(keyword -> new GsonNumberElementSchema(
                        keyword.getArgAsNumber(),
                        required,
                        nullable
                )).orElseGet(() -> new GsonNumberElementSchema(
                        0,
                        required,
                        nullable
                ));
            } else if (keywords.contains(Keyword.fromString("boolean"))) {
                return defaultKeyword.map(keyword -> new GsonBooleanElementSchema(
                        keyword.getArgAsBoolean(),
                        required,
                        nullable
                )).orElseGet(() -> new GsonBooleanElementSchema(
                        false,
                        required,
                        nullable
                ));
            } else if (keywords.contains(Keyword.fromString("string"))) {
                return defaultKeyword.map(keyword -> new GsonStringElementSchema(
                        keyword.getArgAsString(),
                        required,
                        nullable
                )).orElseGet(() -> new GsonStringElementSchema(
                        "",
                        required,
                        nullable
                ));
            } else {
                throw new IllegalArgumentException("No recognised type was specified");
            }
        } else {
            throw new IllegalArgumentException("Expected a string to define type constraints for primitives");
        }
    }

    private GsonArrayElementSchema parseArray(JsonArray jsonArray) throws InvalidKeywordException {
        return new GsonArrayElementSchema(
                parse(jsonArray.get(0)),
                false,
                false
        );
    }

    private GsonObjectElementSchema parseDocument(JsonObject jsonObject) throws InvalidKeywordException {
        GsonObjectElementSchema.GsonDocumentSchemaBuilder builder =
                GsonObjectElementSchema.builder().nullable();
        for (Map.Entry<String, JsonElement> field : jsonObject.entrySet()) {
            builder.add(field.getKey(), parse(field.getValue()));
        }
        return builder.create();
    }

    public GsonElementSchema parse(JsonElement jsonElement) throws InvalidKeywordException {
        GsonElementSchema gsonSchema = null;
        if (jsonElement.isJsonObject()) {
            gsonSchema = parseDocument(jsonElement.getAsJsonObject());
        } else if (jsonElement.isJsonPrimitive()) {
            gsonSchema = parsePrimitive(jsonElement.getAsJsonPrimitive());
        } else if (jsonElement.isJsonArray()) {
            gsonSchema = parseArray(jsonElement.getAsJsonArray());
        } else if (jsonElement.isJsonNull()) {
            throw new IllegalArgumentException("Null is not a valid schema object");
        }
        return gsonSchema;
    }
}

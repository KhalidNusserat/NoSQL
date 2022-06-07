package com.atypon.nosql.gsondocument;

import com.atypon.nosql.keywordsparser.InvalidKeywordException;
import com.atypon.nosql.keywordsparser.Keyword;
import com.atypon.nosql.keywordsparser.KeywordsParser;
import com.google.gson.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GsonElementSchemaParser {
    private final KeywordsParser keywordsParser;

    public GsonElementSchemaParser(KeywordsParser keywordsParser) {
        this.keywordsParser = keywordsParser;
    }

    private GsonPrimitiveSchema<?> parsePrimitive(JsonPrimitive jsonPrimitive) throws InvalidKeywordException {
        if (jsonPrimitive.isString()) {
            String keywordsString = jsonPrimitive.getAsString();
            List<Keyword> keywords = keywordsParser.parse(keywordsString);
            boolean required = keywords.contains(Keyword.fromString("required"));
            boolean nullable = keywords.contains(Keyword.fromString("nullable"));
            Optional<Keyword> defaultKeyword = keywords.stream()
                    .filter(keyword -> keyword.getName().equals("default"))
                    .findFirst();
            if (keywords.contains(Keyword.fromString("number"))) {
                return defaultKeyword.map(keyword -> new GsonNumberSchema(
                        keyword.getArgAsNumber(),
                        required,
                        nullable
                )).orElseGet(() -> new GsonNumberSchema(
                        0,
                        required,
                        nullable
                ));
            } else if (keywords.contains(Keyword.fromString("boolean"))) {
                return defaultKeyword.map(keyword -> new GsonBooleanSchema(
                        keyword.getArgAsBoolean(),
                        required,
                        nullable
                )).orElseGet(() -> new GsonBooleanSchema(
                        false,
                        required,
                        nullable
                ));
            } else if (keywords.contains(Keyword.fromString("string"))) {
                return defaultKeyword.map(keyword -> new GsonStringSchema(
                        keyword.getArgAsString(),
                        required,
                        nullable
                )).orElseGet(() -> new GsonStringSchema(
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

    private GsonArraySchema parseArray(JsonArray jsonArray) throws InvalidKeywordException {
        return new GsonArraySchema(
                parse(jsonArray.get(0)),
                false,
                false
        );
    }

    private GsonObjectSchema parseDocument(JsonObject jsonObject) throws InvalidKeywordException {
        GsonObjectSchema.GsonObjectSchemaBuilder builder =
                GsonObjectSchema.builder().nullable();
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

    public GsonObjectSchema parse(String src) throws InvalidKeywordException {
        Gson gson = new Gson();
        return parseDocument(gson.fromJson(src, JsonObject.class));
    }
}

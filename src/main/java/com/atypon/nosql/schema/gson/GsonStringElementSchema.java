package com.atypon.nosql.schema.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import javax.naming.directory.SchemaViolationException;

public class GsonStringElementSchema extends GsonPrimitiveElementSchema<String> {
    public GsonStringElementSchema(String defaultValue, boolean required, boolean nullable) {
        super(defaultValue, required, nullable);
    }

    @Override
    public JsonElement validate(JsonElement element) throws SchemaViolationException {
        if (element.isJsonNull() && !isNullable()) {
            throw new IllegalArgumentException("Null provided for a non-nullable field");
        }
        if (element.isJsonNull() || element.getAsJsonPrimitive().isString()) {
            return element;
        }
        throw new SchemaViolationException("Not a JsonString: " + element);
    }

    @Override
    public JsonElement getDefault() {
        return new JsonPrimitive(defaultValue);
    }
}

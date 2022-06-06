package com.atypon.nosql.schema.gson.primitives;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import javax.naming.directory.SchemaViolationException;

public class GsonNumberSchema extends GsonPrimitiveSchema<Number> {
    public GsonNumberSchema(Number defaultValue, boolean required, boolean nullable) {
        super(defaultValue, required, nullable);
    }

    @Override
    public JsonElement validate(JsonElement element) throws SchemaViolationException {
        if (element.isJsonNull() && !isNullable()) {
            throw new IllegalArgumentException("Null provided for a non-nullable field");
        }
        if (element.isJsonNull() || element.getAsJsonPrimitive().isNumber()) {
            return element;
        }
        throw new SchemaViolationException("Not a JsonNumber: " + element);
    }

    @Override
    public JsonElement getDefault() {
        return new JsonPrimitive(defaultValue);
    }
}

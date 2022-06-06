package com.atypon.nosql.schema.gson.primitives;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import javax.naming.directory.SchemaViolationException;

public class GsonBooleanSchema extends GsonPrimitiveSchema<Boolean> {
    public GsonBooleanSchema(boolean defaultValue, boolean required, boolean nullable) {
        super(defaultValue, required, nullable);
    }

    @Override
    public JsonElement validate(JsonElement element) throws SchemaViolationException {
        if (element.isJsonNull() && !isNullable()) {
            throw new IllegalArgumentException("Null provided for a non-nullable field");
        }
        if (element.isJsonNull() || element.getAsJsonPrimitive().isBoolean()) {
            return element;
        }
        throw new SchemaViolationException("Not a JsonBoolean: " + element);
    }

    @Override
    public JsonElement getDefault() {
        return new JsonPrimitive(defaultValue);
    }
}

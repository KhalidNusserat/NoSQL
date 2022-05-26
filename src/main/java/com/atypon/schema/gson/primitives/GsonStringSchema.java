package com.atypon.schema.gson.primitives;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import javax.naming.directory.SchemaViolationException;

public class GsonStringSchema extends GsonPrimitiveSchema<String> {
    public GsonStringSchema(String defaultValue, boolean required, boolean nullable) {
        super(defaultValue, required, nullable);
    }

    @Override
    public JsonElement create(JsonPrimitive value) throws SchemaViolationException {
        if (value.isJsonNull() && value.isString()) {
            return value;
        }
        throw new SchemaViolationException();
    }

    @Override
    public JsonElement getDefault() {
        return new JsonPrimitive(defaultValue);
    }
}

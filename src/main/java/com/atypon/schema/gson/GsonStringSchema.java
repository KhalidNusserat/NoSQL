package com.atypon.schema.gson;

import com.atypon.schema.PrimitiveSchema;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import javax.naming.directory.SchemaViolationException;

public class GsonStringSchema extends PrimitiveSchema<JsonElement, JsonPrimitive> {
    private final String defaultValue;

    public GsonStringSchema(String defaultValue) {
        super();
        this.defaultValue = defaultValue;
    }

    public GsonStringSchema(String defaultValue, boolean required, boolean nullable) {
        super(required, nullable);
        this.defaultValue = defaultValue;
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
        return new JsonPrimitive("");
    }
}

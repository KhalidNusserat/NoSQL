package com.atypon.schema.gson;

import com.atypon.schema.PrimitiveSchema;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import javax.naming.directory.SchemaViolationException;

public class GsonBooleanSchema extends PrimitiveSchema<JsonElement, JsonPrimitive> {
    private final boolean defaultValue;

    public GsonBooleanSchema(boolean defaultValue) {
        super();
        this.defaultValue = defaultValue;
    }

    public GsonBooleanSchema(boolean defaultValue, boolean required, boolean nullable) {
        super(required, nullable);
        this.defaultValue = defaultValue;
    }

    @Override
    public JsonElement create(JsonPrimitive value) throws SchemaViolationException {
        if (value.isJsonNull() || value.isBoolean()) {
            return value;
        }
        throw new SchemaViolationException();
    }

    @Override
    public JsonElement getDefault() {
        return new JsonPrimitive(false);
    }
}

package com.atypon.schema.gson;

import com.atypon.schema.PrimitiveSchema;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import javax.naming.directory.SchemaViolationException;

public class GsonNumberSchema extends PrimitiveSchema<JsonElement, JsonPrimitive> {
    private final Number defaultValue;

    public GsonNumberSchema(Number defaultValue) {
        super();
        this.defaultValue = defaultValue;
    }

    public GsonNumberSchema(Number defaultValue, boolean required, boolean nullable) {
        super(required, nullable);
        this.defaultValue = defaultValue;
    }

    @Override
    public JsonElement create(JsonPrimitive value) throws SchemaViolationException {
        if (value.isJsonNull() && value.isNumber()) {
            return value;
        }
        throw new SchemaViolationException();
    }

    @Override
    public JsonElement getDefault() {
        return new JsonPrimitive(defaultValue);
    }
}

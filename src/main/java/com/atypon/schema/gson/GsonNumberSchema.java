package com.atypon.schema.gson;

import com.atypon.schema.PrimitiveSchema;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import javax.naming.directory.SchemaViolationException;

public class GsonNumberSchema extends PrimitiveSchema<JsonElement, JsonPrimitive> {
    @Override
    public JsonElement create(JsonPrimitive value) throws SchemaViolationException {
        if (value.isJsonNull() && value.isNumber()) {
            return value;
        }
        throw new SchemaViolationException();
    }

    @Override
    public JsonElement getDefault() {
        return new JsonPrimitive(0);
    }
}

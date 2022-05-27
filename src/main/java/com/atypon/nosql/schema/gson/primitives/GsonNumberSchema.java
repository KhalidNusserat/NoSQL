package com.atypon.nosql.schema.gson.primitives;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import javax.naming.directory.SchemaViolationException;

public class GsonNumberSchema extends GsonPrimitiveSchema<Number> {
    public GsonNumberSchema(Number defaultValue, boolean required, boolean nullable) {
        super(defaultValue, required, nullable);
    }

    @Override
    public JsonElement create(Object argsObject) throws SchemaViolationException {
        Preconditions.checkState(argsObject instanceof JsonElement);
        JsonElement value = (JsonElement) argsObject;
        if (value.isJsonNull() && !isNullable()) {
            throw new IllegalArgumentException("Not nullable");
        }
        if (value.isJsonNull() || value.getAsJsonPrimitive().isNumber()) {
            return value;
        }
        throw new SchemaViolationException();
    }

    @Override
    public JsonElement getDefault() {
        return new JsonPrimitive(defaultValue);
    }
}

package com.atypon.schema.gson;

import com.atypon.schema.ArraySchema;
import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import javax.naming.directory.SchemaViolationException;

public class GsonArraySchema extends ArraySchema<JsonElement> implements GsonSchema {
    public GsonArraySchema(GsonSchema itemSchema, boolean required, boolean nullable) {
        super(itemSchema, required, nullable);
    }

    @Override
    public JsonElement getDefault() {
        return new JsonArray();
    }

    @Override
    public JsonElement create(Object argsObject) throws SchemaViolationException {
        Preconditions.checkState(argsObject instanceof JsonArray);
        JsonArray args = (JsonArray) argsObject;
        JsonArray array = new JsonArray();
        for (JsonElement jsonElement : args) {
            array.add(itemSchema.create(jsonElement));
        }
        return array;
    }
}

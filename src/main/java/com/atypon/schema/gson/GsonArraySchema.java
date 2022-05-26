package com.atypon.schema.gson;

import com.atypon.schema.ArraySchema;
import com.atypon.schema.Schema;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import javax.naming.directory.SchemaViolationException;

public class GsonArraySchema extends ArraySchema<JsonElement, JsonElement, JsonArray> {
    public GsonArraySchema(Schema<JsonElement, JsonElement> itemSchema) {
        super(itemSchema);
    }

    public GsonArraySchema(Schema<JsonElement, JsonElement> itemSchema, boolean required, boolean nullable) {
        super(itemSchema, required, nullable);
    }

    @Override
    public JsonElement getDefault() {
        return new JsonArray();
    }

    @Override
    public JsonElement create(JsonArray args) throws SchemaViolationException {
        JsonArray array = new JsonArray();
        for (JsonElement jsonElement : args) {
            array.add(itemSchema.create(jsonElement));
        }
        return array;
    }
}

package com.atypon.nosql.schema.gson;

import com.atypon.nosql.schema.ArraySchema;
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
    public JsonElement validate(JsonElement element) throws SchemaViolationException {
        Preconditions.checkState(element.isJsonArray());
        JsonArray args = element.getAsJsonArray();
        JsonArray array = new JsonArray();
        for (JsonElement jsonElement : args) {
            array.add(itemSchema.validate(jsonElement));
        }
        return array;
    }
}

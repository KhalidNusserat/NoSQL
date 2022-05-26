package com.atypon.nosql.schema.gson;

import com.atypon.nosql.gsonwrapper.GsonObject;
import com.atypon.nosql.schema.DocumentSchema;
import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import javax.naming.directory.SchemaViolationException;
import java.util.HashMap;
import java.util.Map;

public class GsonDocumentSchema extends DocumentSchema<JsonElement> implements GsonSchema {
    public GsonDocumentSchema(Map<String, GsonSchema> fields, boolean required, boolean nullable) {
        super(required, nullable);
        this.fields.putAll(fields);
    }

    public static GsonDocumentSchemaBuilder builder() {
        return new GsonDocumentSchemaBuilder();
    }

    @Override
    public JsonElement getDefault() {
        return JsonNull.INSTANCE;
    }

    @Override
    public JsonElement create(Object argsObject) throws SchemaViolationException {
        Preconditions.checkState(argsObject instanceof JsonObject);
        JsonObject args = (JsonObject) argsObject;
        GsonObject.GsonDocumentBuilder builder = GsonObject.builder();
        for (Map.Entry<String, JsonElement> field : args.getAsJsonObject().entrySet()) {
            String fieldName = field.getKey();
            if (this.fields.containsKey(fieldName)) {
                builder.add(fieldName, this.fields.get(fieldName).create(field.getValue()));
            } else {
                throw new SchemaViolationException("Unrecognized field: " + fieldName);
            }
        }
        return builder.create().getAsJsonObject();
    }

    public static class GsonDocumentSchemaBuilder {
        private boolean required = false;

        private boolean nullable = false;

        private final Map<String, GsonSchema> fields = new HashMap<>();

        public GsonDocumentSchemaBuilder required() {
            required = true;
            return this;
        }

        public GsonDocumentSchemaBuilder nullable() {
            nullable = true;
            return this;
        }

        public GsonDocumentSchemaBuilder add(String field, GsonSchema schema) {
            fields.put(field, schema);
            return this;
        }

        public GsonDocumentSchema create() {
            return new GsonDocumentSchema(fields, required, nullable);
        }
    }
}

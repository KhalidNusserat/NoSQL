package com.atypon.nosql.schema.gson;

import com.atypon.nosql.gsondocument.GsonDocument;
import com.atypon.nosql.schema.DocumentElementSchema;
import com.atypon.nosql.schema.ElementSchema;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import javax.naming.directory.SchemaViolationException;
import java.util.HashMap;
import java.util.Map;

public class GsonObjectElementSchema extends DocumentElementSchema<JsonElement> implements GsonElementSchema {
    public GsonObjectElementSchema(Map<String, GsonElementSchema> fields, boolean required, boolean nullable) {
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
    public JsonElement validate(JsonElement element) throws SchemaViolationException {
        if (element.isJsonNull() && isNullable()) {
            return element;
        } else if (element.isJsonNull()) {
            throw new SchemaViolationException("Null provided for a non-nullable field");
        } else if (!element.isJsonObject()) {
            throw new SchemaViolationException("Not a JsonObject: " + element);
        }
        JsonObject document = element.getAsJsonObject();
        GsonDocument.GsonDocumentBuilder builder = GsonDocument.builder();
        for (Map.Entry<String, JsonElement> field : document.entrySet()) {
            String fieldName = field.getKey();
            if (this.fields.containsKey(fieldName)) {
                builder.add(fieldName, this.fields.get(fieldName).validate(field.getValue()));
            } else {
                throw new SchemaViolationException("Unrecognized field: " + fieldName);
            }
        }
        for (Map.Entry<String, ElementSchema<JsonElement>> field : fields.entrySet()) {
            if (!builder.containsKey(field.getKey())) {
                if (field.getValue().isRequired()) {
                    throw new IllegalArgumentException("Missing required field: " + field.getKey());
                } else {
                    builder.add(field.getKey(), field.getValue().getDefault());
                }
            }
        }
        return builder.create().getAsJsonObject();
    }

    public static class GsonDocumentSchemaBuilder {
        private final Map<String, GsonElementSchema> fields = new HashMap<>();
        private boolean required = false;
        private boolean nullable = false;

        public GsonDocumentSchemaBuilder required() {
            required = true;
            return this;
        }

        public GsonDocumentSchemaBuilder nullable() {
            nullable = true;
            return this;
        }

        public GsonDocumentSchemaBuilder add(String field, GsonElementSchema schema) {
            fields.put(field, schema);
            return this;
        }

        public GsonObjectElementSchema create() {
            return new GsonObjectElementSchema(fields, required, nullable);
        }
    }
}

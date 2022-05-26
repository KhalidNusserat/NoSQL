package com.atypon.schema;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DocumentSchema extends Schema {
    private final ConcurrentMap<String, Schema> fields = new ConcurrentHashMap<>();

    public DocumentSchema() {
        super(false, true);
    }

    public DocumentSchema(boolean required, boolean nullable) {
        super(required, nullable);
    }

    @Override
    public Object getDefault() {
        return null;
    }

    public DocumentSchemaBuilder builder() {
        return new DocumentSchemaBuilder();
    }

    public Schema get(String field) {
        return fields.get(field);
    }

    public static class DocumentSchemaBuilder {
        private final DocumentSchema schema = new DocumentSchema();

        public void addField(String field, Schema schema) {
            this.schema.fields.put(field, schema);
        }

        public void remove(String field) {
            schema.fields.remove(field);
        }

        public DocumentSchema create() {
            return schema;
        }
    }
}

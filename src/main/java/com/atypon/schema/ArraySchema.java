package com.atypon.schema;

import javax.naming.directory.SchemaViolationException;

public abstract class ArraySchema<DocumentValue, ItemArgsType, ArrayArgsType>
        extends Schema<DocumentValue, ArrayArgsType> {
    protected final Schema<DocumentValue, ItemArgsType> itemSchema;

    public ArraySchema(Schema<DocumentValue, ItemArgsType> itemSchema) {
        super(false, true);
        this.itemSchema = itemSchema;
    }

    public ArraySchema(Schema<DocumentValue, ItemArgsType> itemSchema, boolean required, boolean nullable) {
        super(required, nullable);
        this.itemSchema = itemSchema;
    }

    @Override
    public abstract DocumentValue create(ArrayArgsType args) throws SchemaViolationException;
}

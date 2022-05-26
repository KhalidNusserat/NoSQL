package com.atypon.schema;

import javax.naming.directory.SchemaViolationException;

public abstract class ArraySchema<DocumentValue, ItemArgsType, ArrayArgsType>
        implements Schema<DocumentValue, ArrayArgsType> {
    protected final Schema<DocumentValue, ItemArgsType> itemSchema;

    private final boolean required;

    private final boolean nullable;

    public ArraySchema(Schema<DocumentValue, ItemArgsType> itemSchema, boolean required, boolean nullable) {
        this.required = required;
        this.nullable = nullable;
        this.itemSchema = itemSchema;
    }

    @Override
    public abstract DocumentValue create(ArrayArgsType args) throws SchemaViolationException;

    @Override
    public boolean isRequired() {
        return required;
    }

    @Override
    public boolean isNullable() {
        return nullable;
    }
}

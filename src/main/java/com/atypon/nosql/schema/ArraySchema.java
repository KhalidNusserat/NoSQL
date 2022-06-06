package com.atypon.nosql.schema;

public abstract class ArraySchema<DocumentElement> implements Schema<DocumentElement> {
    protected final Schema<DocumentElement> itemSchema;

    private final boolean required;

    private final boolean nullable;

    public ArraySchema(Schema<DocumentElement> itemSchema, boolean required, boolean nullable) {
        this.required = required;
        this.nullable = nullable;
        this.itemSchema = itemSchema;
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    @Override
    public boolean isNullable() {
        return nullable;
    }
}

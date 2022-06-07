package com.atypon.nosql.schema;

public abstract class ArrayElementSchema<DocumentElement> implements ElementSchema<DocumentElement> {
    protected final ElementSchema<DocumentElement> itemElementSchema;

    private final boolean required;

    private final boolean nullable;

    public ArrayElementSchema(ElementSchema<DocumentElement> itemElementSchema, boolean required, boolean nullable) {
        this.required = required;
        this.nullable = nullable;
        this.itemElementSchema = itemElementSchema;
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

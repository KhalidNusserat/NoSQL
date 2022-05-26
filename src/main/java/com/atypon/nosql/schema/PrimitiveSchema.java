package com.atypon.nosql.schema;

public abstract class PrimitiveSchema<DocumentElement> implements Schema<DocumentElement> {
    private final boolean required;

    private final boolean nullable;

    public PrimitiveSchema(boolean required, boolean nullable) {
        this.required = required;
        this.nullable = nullable;
    }

    public boolean isRequired() {
        return required;
    }

    public boolean isNullable() {
        return nullable;
    }
}

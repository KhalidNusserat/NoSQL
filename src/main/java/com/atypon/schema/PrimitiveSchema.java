package com.atypon.schema;

public abstract class PrimitiveSchema<DocumentValue, PrimitiveType> implements Schema<DocumentValue, PrimitiveType> {
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

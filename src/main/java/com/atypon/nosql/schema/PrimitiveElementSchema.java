package com.atypon.nosql.schema;

public abstract class PrimitiveElementSchema<DocumentElement> implements ElementSchema<DocumentElement> {
    private final boolean required;

    private final boolean nullable;

    public PrimitiveElementSchema(boolean required, boolean nullable) {
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

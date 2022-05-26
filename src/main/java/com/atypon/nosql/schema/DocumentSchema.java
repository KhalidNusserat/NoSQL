package com.atypon.nosql.schema;

import java.util.HashMap;
import java.util.Map;

public abstract class DocumentSchema<DocumentElement> implements Schema<DocumentElement> {
    protected final Map<String, Schema<DocumentElement>> fields = new HashMap<>();

    private final boolean required;

    private final boolean nullable;

    public DocumentSchema(boolean required, boolean nullable) {
        this.required = required;
        this.nullable = nullable;
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

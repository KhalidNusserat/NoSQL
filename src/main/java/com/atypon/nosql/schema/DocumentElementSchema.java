package com.atypon.nosql.schema;

import java.util.HashMap;
import java.util.Map;

public abstract class DocumentElementSchema<DocumentElement> implements ElementSchema<DocumentElement> {
    protected final Map<String, ElementSchema<DocumentElement>> fields = new HashMap<>();

    private final boolean required;

    private final boolean nullable;

    public DocumentElementSchema(boolean required, boolean nullable) {
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

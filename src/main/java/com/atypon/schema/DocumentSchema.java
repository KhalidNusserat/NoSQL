package com.atypon.schema;

import java.util.HashMap;
import java.util.Map;

public abstract class DocumentSchema<DocumentValue, ArgsType> implements Schema<DocumentValue, ArgsType> {
    protected final Map<String, Schema<DocumentValue, ?>> fields = new HashMap<>();

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

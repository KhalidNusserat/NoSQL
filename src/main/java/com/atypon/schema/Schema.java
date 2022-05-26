package com.atypon.schema;

import javax.naming.directory.SchemaViolationException;

public abstract class Schema<DocumentValue, ArgsType> {
    protected final boolean required;

    protected final boolean nullable;

    public Schema(boolean required, boolean nullable) {
        this.required = required;
        this.nullable = nullable;
    }

    public abstract DocumentValue getDefault();

    public abstract DocumentValue create(ArgsType args) throws SchemaViolationException;
}

package com.atypon.schema;

public abstract class Schema {
    protected final boolean required;

    protected final boolean nullable;

    public Schema(boolean required, boolean nullable) {
        this.required = required;
        this.nullable = nullable;
    }

    public abstract Object getDefault();
}

package com.atypon.schema;

import javax.naming.directory.SchemaViolationException;

public abstract class PrimitiveSchema<DocumentValue, PrimitiveType> extends Schema<DocumentValue, PrimitiveType> {
    public PrimitiveSchema() {
        super(false, true);
    }

    public PrimitiveSchema(boolean required, boolean nullable) {
        super(required, nullable);
    }

    @Override
    public abstract DocumentValue create(PrimitiveType value) throws SchemaViolationException;

    @Override
    public abstract DocumentValue getDefault();
}

package com.atypon.schema;

import java.util.HashMap;
import java.util.Map;

public abstract class DocumentSchema<DocumentValue, ArgsType> extends Schema<DocumentValue, ArgsType> {
    protected final Map<String, Schema<DocumentValue, ?>> fields = new HashMap<>();

    public DocumentSchema() {
        super(false, true);
    }

    public DocumentSchema(boolean required, boolean nullable) {
        super(required, nullable);
    }
}

package com.atypon.schema.gson.primitives;

import com.atypon.schema.PrimitiveSchema;
import com.atypon.schema.gson.GsonSchema;
import com.google.gson.JsonElement;

import javax.naming.directory.SchemaViolationException;

public abstract class GsonPrimitiveSchema<DataType>
        extends PrimitiveSchema<JsonElement>
        implements GsonSchema {
    protected final DataType defaultValue;

    public GsonPrimitiveSchema(DataType defaultValue, boolean required, boolean nullable) {
        super(required, nullable);
        this.defaultValue = defaultValue;
    }

    @Override
    public abstract JsonElement getDefault();

    @Override
    public abstract JsonElement create(Object argsObject) throws SchemaViolationException;
}

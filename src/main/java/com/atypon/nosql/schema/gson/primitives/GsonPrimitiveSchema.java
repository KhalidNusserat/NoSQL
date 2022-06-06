package com.atypon.nosql.schema.gson.primitives;

import com.atypon.nosql.schema.PrimitiveSchema;
import com.atypon.nosql.schema.gson.GsonSchema;
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
}

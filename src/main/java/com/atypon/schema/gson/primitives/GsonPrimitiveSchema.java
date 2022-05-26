package com.atypon.schema.gson.primitives;

import com.atypon.schema.PrimitiveSchema;
import com.atypon.schema.gson.GsonSchema;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import javax.naming.directory.SchemaViolationException;

public abstract class GsonPrimitiveSchema<DataType>
        extends PrimitiveSchema<JsonElement, JsonPrimitive>
        implements GsonSchema<JsonPrimitive>
{
    protected final DataType defaultValue;

    public GsonPrimitiveSchema(DataType defaultValue, boolean required, boolean nullable) {
        super(required, nullable);
        this.defaultValue = defaultValue;
    }

    @Override
    public abstract JsonElement getDefault();

    @Override
    public abstract JsonElement create(JsonPrimitive args) throws SchemaViolationException;
}

package com.atypon.nosql.schema.gson;

import com.atypon.nosql.schema.PrimitiveSchema;
import com.google.gson.JsonElement;

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

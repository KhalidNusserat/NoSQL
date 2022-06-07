package com.atypon.nosql.schema.gson;

import com.atypon.nosql.schema.PrimitiveElementSchema;
import com.google.gson.JsonElement;

public abstract class GsonPrimitiveElementSchema<DataType>
        extends PrimitiveElementSchema<JsonElement>
        implements GsonElementSchema {
    protected final DataType defaultValue;

    public GsonPrimitiveElementSchema(DataType defaultValue, boolean required, boolean nullable) {
        super(required, nullable);
        this.defaultValue = defaultValue;
    }

    @Override
    public abstract JsonElement getDefault();
}

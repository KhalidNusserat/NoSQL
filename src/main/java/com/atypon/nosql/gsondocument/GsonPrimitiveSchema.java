package com.atypon.nosql.gsondocument;

import com.atypon.nosql.schema.PrimitiveElementSchema;
import com.google.gson.JsonElement;

public abstract class GsonPrimitiveSchema<DataType>
        extends PrimitiveElementSchema<JsonElement>
        implements GsonElementSchema {
    protected final DataType defaultValue;

    public GsonPrimitiveSchema(DataType defaultValue, boolean required, boolean nullable) {
        super(required, nullable);
        this.defaultValue = defaultValue;
    }

    @Override
    public abstract JsonElement getDefault();
}

package com.atypon.nosql.gsondocument;

import com.atypon.nosql.document.DocumentSchema;
import com.atypon.nosql.gsondocument.constraints.Constraints;

public class GsonDocumentSchema implements DocumentSchema<GsonDocument> {
    private final Constraints constraints;

    public GsonDocumentSchema(Constraints constraints) {
        this.constraints = constraints;
    }

    public static GsonDocumentSchema from(Constraints constraints) {
        return new GsonDocumentSchema(constraints);
    }

    @Override
    public boolean validate(GsonDocument document) {
        return constraints.validate(document.object);
    }
}

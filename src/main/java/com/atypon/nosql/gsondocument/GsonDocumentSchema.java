package com.atypon.nosql.gsondocument;

import com.atypon.nosql.document.DocumentSchema;
import com.atypon.nosql.gsondocument.constraints.AllMatchConstraint;

public class GsonDocumentSchema implements DocumentSchema<GsonDocument> {
    private final AllMatchConstraint constraints;

    public GsonDocumentSchema(AllMatchConstraint constraints) {
        this.constraints = constraints;
    }

    @Override
    public boolean validate(GsonDocument document) {
        return constraints.validate(document.object);
    }
}

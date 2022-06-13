package com.atypon.nosql.gsondocument;

import com.atypon.nosql.document.DocumentSchema;
import com.atypon.nosql.gsondocument.constraints.Constraints;

public class GsonDocumentSchema implements DocumentSchema<GsonDocument> {
    private final Constraints constraints;

    private final GsonDocument schemaDocument;

    public GsonDocumentSchema(Constraints constraints, GsonDocument schemaDocument) {
        this.constraints = constraints;
        this.schemaDocument = schemaDocument;
    }

    @Override
    public boolean validate(GsonDocument document) {
        return constraints.validate(document.object);
    }

    @Override
    public GsonDocument getAsDocument() {
        return schemaDocument;
    }
}

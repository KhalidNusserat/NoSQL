package com.atypon.nosql.gsondocument;

import com.atypon.nosql.document.Document;
import com.atypon.nosql.document.DocumentSchema;
import com.atypon.nosql.gsondocument.constraints.Constraints;

public class GsonDocumentSchema implements DocumentSchema {
    private final Constraints constraints;

    private final GsonDocument schemaDocument;

    public GsonDocumentSchema(Constraints constraints, GsonDocument schemaDocument) {
        this.constraints = constraints;
        this.schemaDocument = schemaDocument;
    }

    @Override
    public boolean validate(Document document) {
        GsonDocument gsonDocument = (GsonDocument) document;
        return constraints.validate(gsonDocument.object);
    }

    @Override
    public GsonDocument getAsDocument() {
        return schemaDocument;
    }
}

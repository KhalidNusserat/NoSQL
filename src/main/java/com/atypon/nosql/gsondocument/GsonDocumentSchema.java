package com.atypon.nosql.gsondocument;

import com.atypon.nosql.document.Document;
import com.atypon.nosql.document.DocumentSchema;
import com.atypon.nosql.gsondocument.constraints.Constraints;

public class GsonDocumentSchema extends DocumentSchema {

    private final GsonDocument schemaDocument;

    private final Constraints constraints;

    public GsonDocumentSchema(GsonDocument schemaDocument, ConstraintsExtractor constraintsExtractor) {
        this.constraints = constraintsExtractor.extractFromObject(schemaDocument.object);
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

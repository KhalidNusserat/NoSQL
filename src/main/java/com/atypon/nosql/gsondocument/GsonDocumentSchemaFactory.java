package com.atypon.nosql.gsondocument;

import com.atypon.nosql.document.Document;
import com.atypon.nosql.document.DocumentSchemaFactory;
import org.springframework.stereotype.Component;

@Component
public class GsonDocumentSchemaFactory implements DocumentSchemaFactory {

    private final ConstraintsExtractor constraintsExtractor;

    public GsonDocumentSchemaFactory(ConstraintsExtractor constraintsExtractor) {
        this.constraintsExtractor = constraintsExtractor;
    }

    @Override
    public GsonDocumentSchema createSchema(Document schemaDocument) {
        GsonDocument gsonDocument = (GsonDocument) schemaDocument;
        return new GsonDocumentSchema(gsonDocument, constraintsExtractor);
    }
}

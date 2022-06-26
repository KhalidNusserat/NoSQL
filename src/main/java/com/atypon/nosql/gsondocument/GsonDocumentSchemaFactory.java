package com.atypon.nosql.gsondocument;

import com.atypon.nosql.document.Document;
import com.atypon.nosql.document.DocumentSchema;
import com.atypon.nosql.document.DocumentSchemaFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GsonDocumentSchemaFactory implements DocumentSchemaFactory {

    private final ConstraintsExtractor constraintsExtractor;

    @Autowired
    public GsonDocumentSchemaFactory(ConstraintsExtractor constraintsExtractor) {
        this.constraintsExtractor = constraintsExtractor;
        DocumentSchema.setSchemaFactory(this);
    }

    @Override
    public GsonDocumentSchema createFromDocument(Document schemaDocument) {
        GsonDocument gsonDocument = (GsonDocument) schemaDocument;
        return new GsonDocumentSchema(gsonDocument, constraintsExtractor);
    }
}

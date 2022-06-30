package com.atypon.nosql.document;

import com.atypon.nosql.document.constraints.Constraints;
import lombok.ToString;

@ToString
public class DocumentSchema {

    private final Document schemaDocument;

    private final Constraints constraints;

    public DocumentSchema(Document schemaDocument) {
        ConstraintsExtractor constraintsExtractor = new ConstraintsExtractor();
        this.constraints = constraintsExtractor.extractFromObject(schemaDocument.object);
        this.schemaDocument = schemaDocument;
    }

    public static DocumentSchema createFromDocument(Document schemaDocument) {
        return new DocumentSchema(schemaDocument);
    }

    public boolean validate(Document document) {
        return constraints.validate(document.object);
    }

    public Document getAsDocument() {
        return schemaDocument;
    }
}

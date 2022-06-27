package com.atypon.nosql.document;

public abstract class DocumentSchema {

    private static DocumentSchemaFactory schemaFactory;

    public static void setSchemaFactory(DocumentSchemaFactory schemaFactory) {
        DocumentSchema.schemaFactory = schemaFactory;
    }

    public static DocumentSchema createFromDocument(Document schemaDocument) {
        return schemaFactory.createFromDocument(schemaDocument);
    }

    public abstract boolean validate(Document document);

    public abstract Document getAsDocument();
}

package com.atypon.nosql.document;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public abstract class DocumentSchema {

    private static DocumentSchemaFactory schemaFactory;

    @Autowired
    public final void setSchemaFactory(DocumentSchemaFactory schemaFactory) {
        DocumentSchema.schemaFactory = schemaFactory;
    }

    public abstract boolean validate(Document document);

    public abstract Document getAsDocument();

    public static DocumentSchema createFromDocument(Document schemaDocument) {
        return schemaFactory.createFromDocument(schemaDocument);
    }

    public static DocumentSchema createFromClass(Class<?> clazz) {
        return schemaFactory.createFromClass(clazz);
    }
}

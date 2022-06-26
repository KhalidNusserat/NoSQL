package com.atypon.nosql.document;

public interface DocumentSchemaFactory {

    DocumentSchema createFromDocument(Document schemaDocument);
}

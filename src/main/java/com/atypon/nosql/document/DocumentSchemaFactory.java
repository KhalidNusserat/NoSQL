package com.atypon.nosql.document;

public interface DocumentSchemaFactory {
    DocumentSchema createSchema(Document schemaDocument);
}

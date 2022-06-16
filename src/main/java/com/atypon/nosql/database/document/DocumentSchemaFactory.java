package com.atypon.nosql.database.document;

public interface DocumentSchemaFactory {
    DocumentSchema createSchema(Document schemaDocument);
}

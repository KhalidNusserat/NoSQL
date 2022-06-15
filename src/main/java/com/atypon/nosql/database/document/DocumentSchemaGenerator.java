package com.atypon.nosql.database.document;

public interface DocumentSchemaGenerator<T extends Document<?>> {
    DocumentSchema<T> createSchema(T schemaDocument) throws InvalidDocumentSchema;
}

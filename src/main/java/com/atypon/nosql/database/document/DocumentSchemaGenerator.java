package com.atypon.nosql.database.document;

import com.atypon.nosql.database.keywordsparser.InvalidKeywordException;

public interface DocumentSchemaGenerator<T extends Document<?>> {
    DocumentSchema<T> createSchema(T schemaDocument) throws InvalidDocumentSchema, InvalidKeywordException;
}

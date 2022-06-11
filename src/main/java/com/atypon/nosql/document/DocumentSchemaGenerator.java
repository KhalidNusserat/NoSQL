package com.atypon.nosql.document;

import com.atypon.nosql.keywordsparser.InvalidKeywordException;

public interface DocumentSchemaGenerator<T extends Document<?>> {
    DocumentSchema<T> createSchema(T schemaDocument) throws InvalidDocumentSchema, InvalidKeywordException;
}

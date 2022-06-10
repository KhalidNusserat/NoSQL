package com.atypon.nosql.schema;

import com.atypon.nosql.document.Document;
import com.atypon.nosql.keywordsparser.InvalidKeywordException;

public interface DocumentSchemaFactory<T extends Document<?>> {
    DocumentSchema<T> create(T schemaDocument) throws InvalidKeywordException;
}

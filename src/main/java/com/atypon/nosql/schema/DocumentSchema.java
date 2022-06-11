package com.atypon.nosql.schema;

import com.atypon.nosql.document.Document;

import javax.naming.directory.SchemaViolationException;

public interface DocumentSchema<T extends Document<?>> {
    T makeDocumentValid(T document) throws SchemaViolationException;
}

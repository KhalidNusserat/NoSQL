package com.atypon.nosql.schema;

import javax.naming.directory.SchemaViolationException;

public interface Schema<DocumentElement> {
    DocumentElement getDefault();

    DocumentElement create(Object argsObject) throws SchemaViolationException;

    boolean isRequired();

    boolean isNullable();
}

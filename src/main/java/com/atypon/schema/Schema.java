package com.atypon.schema;

import javax.naming.directory.SchemaViolationException;

public interface Schema<DocumentElement> {
    DocumentElement getDefault();

    DocumentElement create(Object argsObject) throws SchemaViolationException;

    boolean isRequired();

    boolean isNullable();
}

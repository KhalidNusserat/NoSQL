package com.atypon.nosql.schema;

import javax.naming.directory.SchemaViolationException;

public interface ElementSchema<DocumentElement> {
    DocumentElement getDefault();

    DocumentElement validate(DocumentElement element) throws SchemaViolationException;

    boolean isRequired();

    boolean isNullable();
}

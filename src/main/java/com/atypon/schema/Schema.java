package com.atypon.schema;

import javax.naming.directory.SchemaViolationException;

public interface Schema<DocumentValue, ArgsType> {
    DocumentValue getDefault();

    DocumentValue create(ArgsType args) throws SchemaViolationException;

    boolean isRequired();

    boolean isNullable();
}

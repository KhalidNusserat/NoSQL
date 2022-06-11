package com.atypon.nosql.document;

public interface DocumentSchema<T extends Document<?>> {
    boolean validate(T document);
}

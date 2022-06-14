package com.atypon.nosql.database.document;

public interface DocumentSchema<T extends Document<?>> {
    boolean validate(T document);

    T getAsDocument();
}

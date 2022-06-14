package com.atypon.nosql.database.document;

public interface DocumentGenerator<T extends Document<?>> {
    T createFromString(String src);

    T appendId(T document);
}

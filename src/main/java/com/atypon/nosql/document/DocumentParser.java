package com.atypon.nosql.document;

public interface DocumentParser<T extends Document<?>> {
    T parse(String src);
}

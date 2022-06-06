package com.atypon.nosql.document;

public interface Document<DocumentElement> {
    ObjectID id();

    DocumentElement get(String field);

    Document<DocumentElement> deepCopy();
}

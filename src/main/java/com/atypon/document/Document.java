package com.atypon.document;

public interface Document<DocumentValue> {
    ObjectID id();

    DocumentValue get(String field);

    Document<DocumentValue> deepCopy();
}

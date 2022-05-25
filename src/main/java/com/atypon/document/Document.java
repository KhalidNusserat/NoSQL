package com.atypon.document;

public interface Document<DocumentValue> {
    long id();

    void add(String property, DocumentValue value);

    void addProperty(String property, double value);

    void addProperty(String property, String value);

    void addProperty(String property, boolean value);

    DocumentValue get(String property);

    DocumentValue remove(String property);

    Document<DocumentValue> deepCopy();
}

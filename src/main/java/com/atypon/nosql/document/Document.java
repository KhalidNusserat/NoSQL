package com.atypon.nosql.document;

public interface Document<DocumentElement> {
    ObjectID id();

    DocumentElement get(String field);

    boolean matches(Document<DocumentElement> bound);

    Document<DocumentElement> withField(String field, DocumentElement element);
}

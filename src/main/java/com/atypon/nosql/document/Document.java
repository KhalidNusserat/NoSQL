package com.atypon.nosql.document;

public interface Document<DocumentElement> {
    String id();

    DocumentElement get(String field);

    boolean matches(Document<DocumentElement> bound);

    Document<DocumentElement> withField(String field, DocumentElement element);

    Document<DocumentElement> withoutField(String field);

    Document<DocumentElement> matchID();
}

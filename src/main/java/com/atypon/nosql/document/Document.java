package com.atypon.nosql.document;

import java.util.Set;

public interface Document<DocumentElement> {
    String id();

    DocumentElement getAll(String field);

    boolean matches(Document<?> matchDocument);

    Document<DocumentElement> withField(String field, DocumentElement element);

    Document<DocumentElement> withoutField(String field);

    Document<DocumentElement> matchID();

    Set<DocumentField> getFields();

    Set<DocumentElement> getAll();

    Set<DocumentElement> getAll(Set<DocumentField> fields);

    DocumentElement get(DocumentField field);
}

package com.atypon.nosql.document;

import java.util.Collection;
import java.util.List;

public interface Document<DocumentElement> {
    String id();

    DocumentElement get(String field);

    boolean matches(Document<DocumentElement> bound);

    Document<DocumentElement> withField(String field, DocumentElement element);

    Document<DocumentElement> withoutField(String field);

    Document<DocumentElement> matchID();

    Collection<List<String>> getFields();
}

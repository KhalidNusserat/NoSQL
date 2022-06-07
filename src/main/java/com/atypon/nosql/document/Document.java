package com.atypon.nosql.document;

import java.util.Map;
import java.util.Set;

public interface Document<DocumentElement> {
    ObjectID id();

    DocumentElement get(String field);

    Document<DocumentElement> deepCopy();

    Set<Map.Entry<String, DocumentElement>> entrySet();
}

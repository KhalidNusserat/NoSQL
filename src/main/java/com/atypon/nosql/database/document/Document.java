package com.atypon.nosql.database.document;

import java.util.Map;

public interface Document {
    boolean subsetOf(Document matchDocument);

    Document getValuesToMatch(Document otherDocument);

    Document getFields();

    Document withField(String field, String value);

    Document withField(String field, Number value);

    Document withField(String field, boolean value);

    Map<String, Object> getAsMap();

    boolean containsField(String field);
}

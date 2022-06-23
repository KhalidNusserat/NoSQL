package com.atypon.nosql.document;

import java.util.Map;

public interface Document {
    boolean subsetOf(Document matchDocument);

    Document getValuesToMatch(Document otherDocument);

    Document getFields();

    Document overrideFields(Document newFieldsValues);

    Map<String, Object> getAsMap();

    boolean containsField(String field);
}

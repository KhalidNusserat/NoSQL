package com.atypon.nosql.document;

import java.util.Map;

public interface Document {

    boolean subsetOf(Document matchDocument);

    Document getValuesToMatch(Document otherDocument);

    Document getFields();

    Document overrideFields(Document newFieldsValues);

    Map<String, Object> toMap();

    <T> T toObject(Class<T> classOfObject);
}

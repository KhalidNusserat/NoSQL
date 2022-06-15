package com.atypon.nosql.database.document;

import com.atypon.nosql.database.gsondocument.FieldsDoNotMatchException;

import java.util.Map;

public interface Document<E> {
    E get(String field);

    Document<E> withField(String field, E element);

    Document<E> withoutField(String field);

    boolean subsetOf(Document<?> matchDocument);

    Document<E> getValuesToMatch(Document<?> otherDocument) throws FieldsDoNotMatchException;

    Document<E> getFields();

    String toString();

    Map<String, Object> getAsMap();
}
package com.atypon.nosql.document;

import com.atypon.nosql.gsondocument.FieldsDoNotMatchException;

import java.util.Map;

public interface Document<E> {
    String id();

    E get(String field);

    Document<E> withField(String field, E element);

    Document<E> withoutField(String field);

    boolean subsetOf(Document<?> matchDocument);

    boolean fieldsSubsetOf(Document<?> matchDocument);

    Document<E> getValuesToMatch(Document<?> otherDocument) throws FieldsDoNotMatchException;

    Document<E> matchId();

    Document<E> getFields();

    String toString();

    Map<String, Object> getAsMap();
}

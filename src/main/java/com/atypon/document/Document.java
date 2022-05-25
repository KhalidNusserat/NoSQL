package com.atypon.document;

import java.math.BigDecimal;
import java.math.BigInteger;

public interface Document<DocumentValue> {
    ObjectID id();

    void add(String property, DocumentValue value);

    void addProperty(String property, BigInteger value);

    void addProperty(String property, BigDecimal value);

    void addProperty(String property, String value);

    void addProperty(String property, boolean value);

    DocumentValue get(String property);

    DocumentValue remove(String property);

    Document<DocumentValue> deepCopy();
}

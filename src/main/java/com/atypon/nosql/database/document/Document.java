package com.atypon.nosql.database.document;

import java.util.Map;

public interface Document {
    boolean subsetOf(Document matchDocument);

    Document getValuesToMatch(Document otherDocument);

    Document getFields();

    String toString();

    Map<String, Object> getAsMap();
}

package com.atypon.nosql.database.document;

import java.util.Collection;
import java.util.Map;

public interface Document {
    boolean subsetOf(Document matchDocument);

    Document getValuesToMatch(Document otherDocument);

    Document getFields();

    String toString();

    Map<String, Object> getAsMap();

    static <T extends Document> Collection<Map<String, Object>> getResultsAsMaps(Collection<T> documents) {
        return documents.stream().map(Document::getAsMap).toList();
    }
}

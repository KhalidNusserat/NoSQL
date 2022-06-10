package com.atypon.nosql.document;

import java.util.Map;
import java.util.Set;

public interface Document<DocumentElement> {
    String id();

    DocumentElement get(String field);

    Set<DocumentElement> getAll();

    DocumentElement get(DocumentField field);

    Set<DocumentElement> getAll(Set<DocumentField> fields);

    Set<DocumentField> getFields();

    Document<DocumentElement> withField(String field, DocumentElement element);

    Document<DocumentElement> withoutField(String field);

    default boolean matches(Document<DocumentElement> matchDocument) {
        Set<DocumentField> matchFields = matchDocument.getFields();
        matchFields.remove(DocumentField.of("_id"));
        Set<DocumentElement> matchValues = matchDocument.getAll(matchFields);
        if (matchFields.contains(DocumentField.of("_matchID"))) {
            matchFields.remove(DocumentField.of("_matchID"));
            matchFields.add(DocumentField.of("_id"));
        }
        return matchValues.equals(getAll(matchFields));
    }

    Document<DocumentElement> matchID();
}

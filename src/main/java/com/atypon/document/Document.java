package com.atypon.document;

public interface Document extends DocumentValue, Iterable<DocumentField> {
    DocumentValue get(DocumentField field);

    String getId();

    Document with(DocumentField field, DocumentValue value);

    Document without(DocumentField field);
}

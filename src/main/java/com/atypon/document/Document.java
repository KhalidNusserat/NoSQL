package com.atypon.document;

import com.google.common.hash.Hashing;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Document implements DocumentValue, Iterable<DocumentField> {
    private final String id;

    private final int hash;

    private final ConcurrentMap<DocumentField, DocumentValue> fields;

    private Document(Map<DocumentField, DocumentValue> fields) {
        this.fields = new ConcurrentHashMap<>(fields);
        this.hash = Objects.hashCode(fields);
        this.id = Hashing.sha256().hashInt(hash).toString();
    }

    public static Document from(Map<DocumentField, DocumentValue> fields) {
        return new Document(fields);
    }

    public static DocumentBuilder builder() {
        return new DocumentBuilder();
    }

    public DocumentValue get(DocumentField field) {
        return fields.get(field);
    }

    public String getId() {
        return id;
    }

    public Document with(DocumentField field, DocumentValue value) {
        Map<DocumentField, DocumentValue> newFields = new HashMap<>(fields);
        newFields.put(field, value);
        return Document.from(newFields);
    }

    public Document without(DocumentField field) {
        Map<DocumentField, DocumentValue> newFields = new HashMap<>(fields);
        newFields.remove(field);
        return Document.from(newFields);
    }

    @Override
    public Iterator<DocumentField> iterator() {
        return fields.keySet().iterator();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Document document = (Document) o;
        return hash == document.hash;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    public static class DocumentBuilder {
        private final Map<DocumentField, DocumentValue> fields = new HashMap<>();

        public DocumentBuilder put(DocumentField field, DocumentValue value) {
            fields.put(field, value);
            return this;
        }

        public Document build() {
            return new Document(fields);
        }
    }
}

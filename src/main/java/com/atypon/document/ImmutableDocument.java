package com.atypon.document;

import com.google.common.hash.Hashing;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ImmutableDocument implements Document {
    private final String id;

    private final int hash;

    private final ConcurrentMap<DocumentField, DocumentValue> fields;

    private ImmutableDocument(Map<DocumentField, DocumentValue> fields) {
        this.fields = new ConcurrentHashMap<>(fields);
        this.hash = Objects.hashCode(fields);
        this.id = Hashing.sha256().hashInt(hash).toString();
    }

    public static DocumentBuilder builder() {
        return new DocumentBuilder();
    }

    public static Document from(Map<DocumentField, DocumentValue> fields) {
        return new ImmutableDocument(fields);
    }

    @Override
    public DocumentValue get(DocumentField field) {
        return fields.get(field);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Document with(DocumentField field, DocumentValue value) {
        Map<DocumentField, DocumentValue> newFields = new HashMap<>(fields);
        newFields.put(field, value);
        return ImmutableDocument.from(newFields);
    }

    @Override
    public Document without(DocumentField field) {
        Map<DocumentField, DocumentValue> newFields = new HashMap<>(fields);
        newFields.remove(field);
        return ImmutableDocument.from(newFields);
    }

    @Override
    public Iterator<DocumentField> iterator() {
        return fields.keySet().iterator();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImmutableDocument immutableDocument = (ImmutableDocument) o;
        return hash == immutableDocument.hash;
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
            return new ImmutableDocument(fields);
        }
    }
}

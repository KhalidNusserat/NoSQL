package com.atypon.document;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Document implements DocumentValue {
    private final ConcurrentMap<DocumentField, DocumentValue> fields;

    private Document() {
        fields = new ConcurrentHashMap<>();
    }

    private Document(Map<DocumentField, DocumentValue> fields) {
        this.fields = new ConcurrentHashMap<>(fields);
    }

    private DocumentValue get(DocumentField field) {
        return fields.get(field);
    }

    public static class DocumentBuilder {
        private final Map<DocumentField, DocumentValue> fields = new HashMap<>();

        private DocumentBuilder(){
        }

        public void put(DocumentField field, DocumentValue value) {
            fields.put(field, value);
        }

        public Document build() {
            return new Document(fields);
        }
    }
}

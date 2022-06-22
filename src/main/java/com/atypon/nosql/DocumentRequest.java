package com.atypon.nosql;

import java.util.Collection;
import java.util.Map;

public record DocumentRequest(
        String database,
        String collection,
        DocumentOperation operation,
        DocumentType documentType,
        Map<String, Object> criteria,
        Collection<Map<String, Object>> documents) {

    public static DocumentRequestBuilder builder() {
        return new DocumentRequestBuilder();
    }

    public static class DocumentRequestBuilder {

        private String database;

        private String collection;

        private DocumentOperation operation;

        private DocumentType documentType;

        private Map<String, Object> criteria;

        private Collection<Map<String, Object>> documents;

        public DocumentRequestBuilder setDatabase(String database) {
            this.database = database;
            return this;
        }

        public DocumentRequestBuilder setCollection(String collection) {
            this.collection = collection;
            return this;
        }

        public DocumentRequestBuilder setOperation(DocumentOperation operation) {
            this.operation = operation;
            return this;
        }

        public DocumentRequestBuilder setDocumentType(DocumentType documentType) {
            this.documentType = documentType;
            return this;
        }

        public DocumentRequestBuilder setCriteria(Map<String, Object> criteria) {
            this.criteria = criteria;
            return this;
        }

        public DocumentRequestBuilder setDocuments(Collection<Map<String, Object>> documents) {
            this.documents = documents;
            return this;
        }

        public DocumentRequest createDocumentRequest() {
            return new DocumentRequest(database, collection, operation, documentType, criteria, documents);
        }
    }
}

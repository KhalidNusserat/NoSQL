package com.atypon.nosql;

import java.util.Collection;
import java.util.Map;

public record DatabaseRequest(
        String database,
        String collection,
        DatabaseOperation operation,
        PayloadType payloadType,
        Map<String, Object> criteria,
        Collection<Map<String, Object>> documents) {

    public static DocumentRequestBuilder builder() {
        return new DocumentRequestBuilder();
    }

    public static class DocumentRequestBuilder {

        private String database;

        private String collection;

        private DatabaseOperation operation;

        private PayloadType payloadType;

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

        public DocumentRequestBuilder setOperation(DatabaseOperation operation) {
            this.operation = operation;
            return this;
        }

        public DocumentRequestBuilder setDocumentType(PayloadType payloadType) {
            this.payloadType = payloadType;
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

        public DatabaseRequest createDocumentRequest() {
            return new DatabaseRequest(database, collection, operation, payloadType, criteria, documents);
        }
    }
}

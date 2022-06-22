package com.atypon.nosql;

import com.atypon.nosql.database.document.Document;

import java.util.List;

public record DocumentRequest(
        String database,
        String collection,
        DocumentOperation operation,
        DocumentType documentType,
        Document criteria,
        List<Document> documents) {

    public static DocumentRequestBuilder builder() {
        return new DocumentRequestBuilder();
    }

    public static class DocumentRequestBuilder {

        private String database;

        private String collection;

        private DocumentOperation operation;

        private DocumentType documentType;

        private Document criteria;

        private List<Document> documents;

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

        public DocumentRequestBuilder setCriteria(Document criteria) {
            this.criteria = criteria;
            return this;
        }

        public DocumentRequestBuilder setDocuments(List<Document> documents) {
            this.documents = documents;
            return this;
        }

        public DocumentRequest createDocumentRequest() {
            return new DocumentRequest(database, collection, operation, documentType, criteria, documents);
        }
    }
}

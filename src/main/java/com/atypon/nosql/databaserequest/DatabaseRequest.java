package com.atypon.nosql.databaserequest;

public record DatabaseRequest(String database, String collection, DatabaseOperation operation, Payload payload) {

    public static DocumentRequestBuilder builder() {
        return new DocumentRequestBuilder();
    }

    public static class DocumentRequestBuilder {

        private String database;

        private String collection;

        private DatabaseOperation operation;

        private Payload payload;

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

        public DocumentRequestBuilder setPayload(Payload payload) {
            this.payload = payload;
            return this;
        }

        public DatabaseRequest createDocumentRequest() {
            return new DatabaseRequest(database, collection, operation, payload);
        }
    }
}

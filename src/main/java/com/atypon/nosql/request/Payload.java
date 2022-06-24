package com.atypon.nosql.request;

import java.util.List;
import java.util.Map;

public record Payload(Map<String, Object> criteria,
                      List<Map<String, Object>> documents,
                      Map<String, Object> index,
                      boolean uniqueIndex,
                      Map<String, Object> update,
                      Map<String, Object> schema) {

    public static PayloadBuilder builder() {
        return new PayloadBuilder();
    }

    public static class PayloadBuilder {

        private Map<String, Object> criteria;

        private List<Map<String, Object>> documents;

        private Map<String, Object> index;

        private boolean uniqueIndex;

        private Map<String, Object> update;

        private Map<String, Object> schema;

        public PayloadBuilder setCriteria(Map<String, Object> criteria) {
            this.criteria = criteria;
            return this;
        }

        public PayloadBuilder setDocuments(List<Map<String, Object>> documents) {
            this.documents = documents;
            return this;
        }

        public PayloadBuilder setIndex(Map<String, Object> index) {
            this.index = index;
            return this;
        }

        public PayloadBuilder setUniqueIndex(boolean uniqueIndex) {
            this.uniqueIndex = uniqueIndex;
            return this;
        }

        public PayloadBuilder setUpdate(Map<String, Object> update) {
            this.update = update;
            return this;
        }

        public PayloadBuilder setSchema(Map<String, Object> schema) {
            this.schema = schema;
            return this;
        }

        public Payload createPayload() {
            return new Payload(criteria, documents, index, uniqueIndex, update, schema);
        }
    }
}

package com.atypon.nosql.databaserequest;

import java.util.List;
import java.util.Map;

public record Payload(Map<String, Object> criteria,
                      List<Map<String, Object>> documents,
                      Map<String, Object> index,
                      Map<String, Object> update) {

    public static PayloadBuilder builder() {
        return new PayloadBuilder();
    }

    public static class PayloadBuilder {
        private Map<String, Object> criteria;
        private List<Map<String, Object>> documents;
        private Map<String, Object> index;
        private Map<String, Object> update;

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

        public PayloadBuilder setUpdate(Map<String, Object> update) {
            this.update = update;
            return this;
        }

        public Payload createPayload() {
            return new Payload(criteria, documents, index, update);
        }
    }
}

package com.atypon.nosql.databaserequest;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public record Payload(Map<String, Object> criteria,
                      List<Map<String, Object>> documents,
                      Map<String, Object> index,
                      Map<String, Object> update) {

    private static final Set<Set<String>> validStates = Set.of(
            Set.of("documents"),
            Set.of("criteria"),
            Set.of("criteria", "update"),
            Set.of("index"),
            Set.of()
    );

    public Payload(
            Map<String, Object> criteria,
            List<Map<String, Object>> documents,
            Map<String, Object> index,
            Map<String, Object> update) {
        this.criteria = criteria;
        this.documents = documents;
        this.index = index;
        this.update = update;
        Set<String> notNullFields = new HashSet<>();
        if (criteria != null) {
            notNullFields.add("criteria");
        }
        if (documents != null) {
            notNullFields.add("documents");
        }
        if (index != null) {
            notNullFields.add("index");
        }
        if (update != null) {
            notNullFields.add("update");
        }
        if (!validStates.contains(notNullFields)) {
            throw new RuntimeException("Invalid payload");
        }
    }

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

package com.atypon.nosql;

import java.util.Collection;
import java.util.Map;

public interface DocumentRequestFactory {
    DocumentRequest addDocuments(
            String database,
            String collection,
            Collection<Map<String, Object>> documents
    );

    DocumentRequest readDocuments(
            String database,
            String collection,
            Map<String, Object> criteria
    );

    DocumentRequest removeDocuments(
            String database,
            String collection,
            Map<String, Object> criteria
    );

    DocumentRequest updateDocuments(
            String database,
            String collection,
            Map<String, Object> criteria,
            Map<String, Object> update
    );
}

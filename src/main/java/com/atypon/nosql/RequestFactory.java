package com.atypon.nosql;

import java.util.List;
import java.util.Map;

public interface RequestFactory {
    DocumentRequest addDocuments(
            String database,
            String collection,
            List<Map<String, Object>> document
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

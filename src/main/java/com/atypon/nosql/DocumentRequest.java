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
}

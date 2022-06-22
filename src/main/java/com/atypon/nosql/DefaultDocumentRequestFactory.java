package com.atypon.nosql;

import com.atypon.nosql.database.document.IdGenerator;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DefaultDocumentRequestFactory implements DocumentRequestFactory {

    private final IdGenerator idGenerator;

    public DefaultDocumentRequestFactory(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    public DocumentRequest addDocuments(String database, String collection, Collection<Map<String, Object>> documents) {
        Collection<Map<String, Object>> parsedDocuments = documents.stream()
                .map(this::appendId)
                .toList();
        return DocumentRequest.builder()
                .setDatabase(database)
                .setCollection(collection)
                .setOperation(DocumentOperation.ADD)
                .setDocumentType(DocumentType.DOCUMENT)
                .setDocuments(parsedDocuments)
                .createDocumentRequest();
    }

    private Map<String, Object> appendId(Map<String, Object> document) {
        Map<String, Object> documentWithId = new HashMap<>(document);
        documentWithId.put("_id", idGenerator.newId(document));
        return documentWithId;
    }

    @Override
    public DocumentRequest readDocuments(String database, String collection, Map<String, Object> criteria) {
        return DocumentRequest.builder()
                .setDatabase(database)
                .setCollection(collection)
                .setOperation(DocumentOperation.READ)
                .setDocumentType(DocumentType.NONE)
                .setCriteria(criteria)
                .createDocumentRequest();
    }

    @Override
    public DocumentRequest removeDocuments(String database, String collection, Map<String, Object> criteria) {
        return DocumentRequest.builder()
                .setDatabase(database)
                .setCollection(collection)
                .setOperation(DocumentOperation.REMOVE)
                .setDocumentType(DocumentType.NONE)
                .setCriteria(criteria)
                .createDocumentRequest();
    }

    @Override
    public DocumentRequest updateDocuments(
            String database,
            String collection,
            Map<String, Object> criteria,
            Map<String, Object> update) {
        return DocumentRequest.builder()
                .setDatabase(database)
                .setCollection(collection)
                .setOperation(DocumentOperation.UPDATE)
                .setDocumentType(DocumentType.UPDATE)
                .setDocuments(List.of(update))
                .setCriteria(criteria)
                .createDocumentRequest();
    }
}

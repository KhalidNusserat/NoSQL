package com.atypon.nosql;

import com.atypon.nosql.database.document.Document;
import com.atypon.nosql.database.document.DocumentFactory;
import com.atypon.nosql.database.document.DocumentIdGenerator;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class DefaultRequestFactory implements RequestFactory {
    private final DocumentFactory documentFactory;

    private final DocumentIdGenerator idGenerator;

    public DefaultRequestFactory(DocumentFactory documentFactory, DocumentIdGenerator idGenerator) {
        this.documentFactory = documentFactory;
        this.idGenerator = idGenerator;
    }

    @Override
    public DocumentRequest addDocuments(String database, String collection, List<Map<String, Object>> documents) {
        List<Document> parsedDocuments = documents.stream()
                .map(documentFactory::createFromMap)
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

    private Document appendId(Document document) {
        return document.withField("_id", idGenerator.newId(document));
    }

    @Override
    public DocumentRequest readDocuments(String database, String collection, Map<String, Object> criteria) {
        Document criteriaDocument = documentFactory.createFromMap(criteria);
        return DocumentRequest.builder()
                .setDatabase(database)
                .setCollection(collection)
                .setOperation(DocumentOperation.READ)
                .setDocumentType(DocumentType.NONE)
                .setCriteria(criteriaDocument)
                .createDocumentRequest();
    }

    @Override
    public DocumentRequest removeDocuments(String database, String collection, Map<String, Object> criteria) {
        Document criteriaDocument = documentFactory.createFromMap(criteria);
        return DocumentRequest.builder()
                .setDatabase(database)
                .setCollection(collection)
                .setOperation(DocumentOperation.REMOVE)
                .setDocumentType(DocumentType.NONE)
                .setCriteria(criteriaDocument)
                .createDocumentRequest();
    }

    @Override
    public DocumentRequest updateDocuments(
            String database,
            String collection,
            Map<String, Object> criteria,
            Map<String, Object> update) {
        Document criteriaDocument = documentFactory.createFromMap(criteria);
        Document updateDocument = documentFactory.createFromMap(update);
        return DocumentRequest.builder()
                .setDatabase(database)
                .setCollection(collection)
                .setOperation(DocumentOperation.UPDATE)
                .setDocumentType(DocumentType.UPDATE)
                .setDocuments(List.of(updateDocument))
                .setCriteria(criteriaDocument)
                .createDocumentRequest();
    }
}

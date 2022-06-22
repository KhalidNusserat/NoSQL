package com.atypon.nosql;

import com.atypon.nosql.database.document.IdGenerator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class DefaultDocumentRequestFormatter implements DocumentRequestFormatter {

    private final IdGenerator idGenerator;

    public DefaultDocumentRequestFormatter(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    public DocumentRequest format(DocumentRequest request) {
        if (request.operation() == DocumentOperation.ADD) {
            List<Map<String, Object>> documents = new ArrayList<>(request.documents());
            documents.forEach(document -> document.put("_id", idGenerator.newId(document)));
            return DocumentRequest.builder()
                    .setDatabase(request.database())
                    .setCollection(request.collection())
                    .setOperation(request.operation())
                    .setDocumentType(request.documentType())
                    .setCriteria(request.criteria())
                    .setDocuments(documents)
                    .createDocumentRequest();
        }
        return request;
    }
}

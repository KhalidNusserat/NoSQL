package com.atypon.nosql.request.handlers;

import com.atypon.nosql.document.Document;
import com.atypon.nosql.request.DatabaseOperation;
import com.atypon.nosql.request.DatabaseRequest;
import com.atypon.nosql.request.Payload;
import com.atypon.nosql.request.annotations.DatabaseOperationMapping;
import com.atypon.nosql.response.DatabaseResponse;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
public class DocumentOperationsHandlers extends OperationsHandlers {

    @DatabaseOperationMapping(DatabaseOperation.ADD_DOCUMENT)
    public DatabaseResponse addDocuments(DatabaseRequest request) {
        Payload payload = request.payload();
        List<Document> documents = payload.documents().stream().toList();
        List<?> result = databasesManager.getDatabase(request.database())
                .getCollection(request.collection())
                .addAll(documents);
        return DatabaseResponse.builder()
                .message("Added [" + result.size() + "] documents")
                .build();
    }

    @DatabaseOperationMapping(DatabaseOperation.READ_DOCUMENTS)
    public DatabaseResponse readDocuments(DatabaseRequest request) {
        Payload payload = request.payload();
        Collection<Map<String, Object>> documents = databasesManager.getDatabase(request.database())
                .getCollection(request.collection())
                .findAll(payload.criteria())
                .stream()
                .map(Document::toMap)
                .toList();
        return DatabaseResponse.builder()
                .message("Found [" + documents.size() + "] docuemnts")
                .result(documents)
                .build();
    }

    @DatabaseOperationMapping(DatabaseOperation.REMOVE_DOCUMENTS)
    public DatabaseResponse removeDocuments(DatabaseRequest request) {
        Payload payload = request.payload();
        int removedCount = databasesManager.getDatabase(request.database())
                .getCollection(request.collection())
                .removeAll(payload.criteria());
        return DatabaseResponse.builder()
                .message(String.format("Removed [%d] documents", removedCount))
                .build();
    }

    @DatabaseOperationMapping(DatabaseOperation.UPDATE_DOCUMENTS)
    public DatabaseResponse updateDocuments(DatabaseRequest request) {
        Payload payload = request.payload();
        int updatedCount = databasesManager.getDatabase(request.database())
                .getCollection(request.collection())
                .updateAll(
                        payload.criteria(),
                        payload.update()
                ).size();
        return DatabaseResponse.builder()
                .message("Updated [" + updatedCount + "] documents")
                .build();
    }
}

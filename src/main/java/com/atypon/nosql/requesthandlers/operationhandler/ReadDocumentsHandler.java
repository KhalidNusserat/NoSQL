package com.atypon.nosql.requesthandlers.operationhandler;

import com.atypon.nosql.document.Document;
import com.atypon.nosql.request.DatabaseOperation;
import com.atypon.nosql.request.DatabaseRequest;
import com.atypon.nosql.request.Payload;
import com.atypon.nosql.response.DatabaseResponse;
import com.atypon.nosql.requesthandlers.DatabaseRequestHandler;
import com.atypon.nosql.requesthandlers.StorageHandler;
import com.atypon.nosql.DatabasesManager;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

@Component
public class ReadDocumentsHandler implements DatabaseRequestHandler {

    private final DatabasesManager databasesManager;

    public ReadDocumentsHandler(StorageHandler storageHandler, DatabasesManager databasesManager) {
        this.databasesManager = databasesManager;
        storageHandler.registerOperationHandler(DatabaseOperation.READ_DOCUMENTS, this);
    }

    @Override
    public DatabaseResponse handle(DatabaseRequest request) {
        Payload payload = request.payload();
        Collection<Map<String, Object>> documents = databasesManager.getDatabase(request.database())
                .getCollection(request.collection())
                .findDocuments(payload.criteria())
                .stream()
                .map(Document::toMap)
                .toList();
        return DatabaseResponse.builder()
                .message("Found [" + documents.size() + "] docuemnts")
                .result(documents)
                .build();
    }
}

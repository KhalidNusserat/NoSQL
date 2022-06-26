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

import java.util.List;

@Component
public class AddDocumentsHandler implements DatabaseRequestHandler {

    private final DatabasesManager databasesManager;

    public AddDocumentsHandler(StorageHandler storageHandler, DatabasesManager databasesManager) {
        this.databasesManager = databasesManager;
        storageHandler.registerOperationHandler(DatabaseOperation.ADD_DOCUMENT, this);
    }

    @Override
    public DatabaseResponse handle(DatabaseRequest request) {
        Payload payload = request.payload();
        List<Document> documents = payload.documents().stream().toList();
        List<?> result = databasesManager.getDatabase(request.database())
                .getCollection(request.collection())
                .addDocuments(documents);
        return DatabaseResponse.builder()
                .message("Added [" + result.size() + "] documents")
                .build();
    }
}

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

@Component
public class UpdateDocumentsHandler implements DatabaseRequestHandler {

    private final DatabasesManager databasesManager;

    public UpdateDocumentsHandler(StorageHandler storageHandler, DatabasesManager databasesManager) {
        this.databasesManager = databasesManager;
        storageHandler.registerOperationHandler(DatabaseOperation.UPDATE_DOCUMENTS, this);
    }

    @Override
    public DatabaseResponse handle(DatabaseRequest request) {
        Payload payload = request.payload();
        int updatedCount = databasesManager.getDatabase(request.database())
                .getCollection(request.collection())
                .updateDocuments(
                        Document.fromMap(payload.criteria()),
                        Document.fromMap(payload.update())
                ).size();
        return DatabaseResponse.builder()
                .message("Updated [" + updatedCount + "] documents")
                .build();
    }
}

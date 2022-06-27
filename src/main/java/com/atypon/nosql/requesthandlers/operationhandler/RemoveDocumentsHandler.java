package com.atypon.nosql.requesthandlers.operationhandler;

import com.atypon.nosql.DatabasesManager;
import com.atypon.nosql.request.DatabaseOperation;
import com.atypon.nosql.request.DatabaseRequest;
import com.atypon.nosql.request.Payload;
import com.atypon.nosql.requesthandlers.DatabaseRequestHandler;
import com.atypon.nosql.requesthandlers.StorageHandler;
import com.atypon.nosql.response.DatabaseResponse;
import org.springframework.stereotype.Component;

@Component
public class RemoveDocumentsHandler implements DatabaseRequestHandler {

    private final DatabasesManager databasesManager;

    public RemoveDocumentsHandler(StorageHandler storageHandler, DatabasesManager databasesManager) {
        this.databasesManager = databasesManager;
        storageHandler.registerOperationHandler(DatabaseOperation.REMOVE_DOCUMENTS, this);
    }

    @Override
    public DatabaseResponse handle(DatabaseRequest request) {
        Payload payload = request.payload();
        int removedCount = databasesManager.getDatabase(request.database())
                .getCollection(request.collection())
                .removeAllThatMatch(payload.criteria());
        return DatabaseResponse.builder()
                .message(String.format("Removed [%d] documents", removedCount))
                .build();
    }
}

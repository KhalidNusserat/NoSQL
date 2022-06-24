package com.atypon.nosql.requesthandlers.operationhandler;

import com.atypon.nosql.request.DatabaseOperation;
import com.atypon.nosql.request.DatabaseRequest;
import com.atypon.nosql.request.Payload;
import com.atypon.nosql.response.DatabaseResponse;
import com.atypon.nosql.requesthandlers.DatabaseRequestHandler;
import com.atypon.nosql.requesthandlers.StorageHandler;
import com.atypon.nosql.DatabasesManager;
import org.springframework.stereotype.Component;

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
        databasesManager.addDocuments(
                request.database(),
                request.collection(),
                payload.documents()
        );
        return DatabaseResponse.createDatabaseResponse(
                String.format("Added [%d] documents", payload.documents().size()),
                null
        );
    }
}

package com.atypon.nosql.requesthandlers.operationhandler;

import com.atypon.nosql.databaserequest.DatabaseOperation;
import com.atypon.nosql.databaserequest.DatabaseRequest;
import com.atypon.nosql.databaserequest.Payload;
import com.atypon.nosql.databaseresponse.DatabaseResponse;
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
        int updatedCount = databasesManager.updateDocuments(
                request.database(),
                request.collection(),
                payload.criteria(),
                payload.update()
        );
        return DatabaseResponse.createDatabaseResponse(
                String.format("Updated [%d] documents", updatedCount),
                null
        );
    }
}

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
public class CreateIndexHandler implements DatabaseRequestHandler {

    private final DatabasesManager databasesManager;

    public CreateIndexHandler(StorageHandler storageHandler, DatabasesManager databasesManager) {
        this.databasesManager = databasesManager;
        storageHandler.registerOperationHandler(DatabaseOperation.CREATE_INDEX, this);
    }

    @Override
    public DatabaseResponse handle(DatabaseRequest request) {
        Payload payload = request.payload();
        databasesManager.createIndex(
                request.database(),
                request.collection(),
                payload.index(),
                payload.uniqueIndex()
        );
        return DatabaseResponse.createDatabaseResponse(
                String.format(
                        "Created new index for the collection \"%s/%s\"",
                        request.database(),
                        request.collection()
                ),
                null
        );
    }
}

package com.atypon.nosql.requesthandlers.operationhandler;

import com.atypon.nosql.databaserequest.DatabaseOperation;
import com.atypon.nosql.databaserequest.DatabaseRequest;
import com.atypon.nosql.databaseresponse.DatabaseResponse;
import com.atypon.nosql.requesthandlers.DatabaseRequestHandler;
import com.atypon.nosql.requesthandlers.StorageHandler;
import com.atypon.nosql.DatabasesManager;
import org.springframework.stereotype.Component;

@Component
public class RemoveCollectionHandler implements DatabaseRequestHandler {

    private final DatabasesManager databasesManager;

    public RemoveCollectionHandler(StorageHandler storageHandler, DatabasesManager databasesManager) {
        this.databasesManager = databasesManager;
        storageHandler.registerOperationHandler(DatabaseOperation.REMOVE_COLLECTION, this);
    }

    @Override
    public DatabaseResponse handle(DatabaseRequest request) {
        databasesManager.removeCollection(
                request.database(),
                request.collection()
        );
        return DatabaseResponse.createDatabaseResponse(
                String.format("Removed the collection \"%s/%s\"", request.database(), request.collection()),
                null
        );
    }
}

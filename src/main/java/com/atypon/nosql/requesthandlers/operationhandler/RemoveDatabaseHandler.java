package com.atypon.nosql.requesthandlers.operationhandler;

import com.atypon.nosql.request.DatabaseOperation;
import com.atypon.nosql.request.DatabaseRequest;
import com.atypon.nosql.response.DatabaseResponse;
import com.atypon.nosql.requesthandlers.DatabaseRequestHandler;
import com.atypon.nosql.requesthandlers.StorageHandler;
import com.atypon.nosql.DatabasesManager;
import org.springframework.stereotype.Component;

@Component
public class RemoveDatabaseHandler implements DatabaseRequestHandler {

    private final DatabasesManager databasesManager;

    public RemoveDatabaseHandler(StorageHandler storageHandler, DatabasesManager databasesManager) {
        this.databasesManager = databasesManager;
        storageHandler.registerOperationHandler(DatabaseOperation.REMOVE_DATABASE, this);
    }

    @Override
    public DatabaseResponse handle(DatabaseRequest request) {
        databasesManager.removeDatabase(request.database());
        return DatabaseResponse.builder()
                .message(String.format("Removed the database \"%s\"", request.database()))
                .build();
    }
}

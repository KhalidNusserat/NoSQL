package com.atypon.nosql.requesthandlers.operationhandler;

import com.atypon.nosql.DatabasesManager;
import com.atypon.nosql.request.DatabaseOperation;
import com.atypon.nosql.request.DatabaseRequest;
import com.atypon.nosql.requesthandlers.DatabaseRequestHandler;
import com.atypon.nosql.requesthandlers.StorageHandler;
import com.atypon.nosql.response.DatabaseResponse;
import org.springframework.stereotype.Component;

@Component
public class CreateDatabaseHandler implements DatabaseRequestHandler {

    private final DatabasesManager databasesManager;

    public CreateDatabaseHandler(StorageHandler storageHandler, DatabasesManager databasesManager) {
        this.databasesManager = databasesManager;
        storageHandler.registerOperationHandler(DatabaseOperation.CREATE_DATABASE, this);
    }

    @Override
    public DatabaseResponse handle(DatabaseRequest request) {
        databasesManager.createDatabase(request.database());
        return DatabaseResponse.builder()
                .message(String.format("Created the database <%s>", request.database()))
                .build();
    }
}

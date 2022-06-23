package com.atypon.nosql.requesthandlers.operationhandler;

import com.atypon.nosql.databaserequest.DatabaseOperation;
import com.atypon.nosql.databaserequest.DatabaseRequest;
import com.atypon.nosql.databaseresponse.DatabaseResponse;
import com.atypon.nosql.requesthandlers.DatabaseRequestHandler;
import com.atypon.nosql.requesthandlers.StorageHandler;
import com.atypon.nosql.DatabasesManager;
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
        databasesManager.createDatabase(
                request.database()
        );
        return DatabaseResponse.createDatabaseResponse(
                String.format("Created the database \"%s\"", request.database()),
                null
        );
    }
}
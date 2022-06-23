package com.atypon.nosql.requesthandlers.operationhandler;

import com.atypon.nosql.DatabasesManager;
import com.atypon.nosql.databaserequest.DatabaseOperation;
import com.atypon.nosql.databaserequest.DatabaseRequest;
import com.atypon.nosql.databaseresponse.DatabaseResponse;
import com.atypon.nosql.requesthandlers.DatabaseRequestHandler;
import com.atypon.nosql.requesthandlers.StorageHandler;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
public class GetDatabasesHandler implements DatabaseRequestHandler {

    private final DatabasesManager databasesManager;

    public GetDatabasesHandler(StorageHandler storageHandler, DatabasesManager databasesManager) {
        this.databasesManager = databasesManager;
        storageHandler.registerOperationHandler(DatabaseOperation.GET_DATABASES, this);
    }

    @Override
    public DatabaseResponse handle(DatabaseRequest request) {
        Collection<Map<String, Object>> databasesNames = databasesManager.getDatabasesNames().stream()
                .map(databaseName -> Map.of("database", (Object) databaseName))
                .toList();
        return DatabaseResponse.createDatabaseResponse(
                String.format("Found [%d] databases", databasesNames.size()),
                databasesNames
        );
    }
}
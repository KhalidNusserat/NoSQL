package com.atypon.nosql.requesthandlers.operationhandler;

import com.atypon.nosql.DatabasesManager;
import com.atypon.nosql.request.DatabaseOperation;
import com.atypon.nosql.request.DatabaseRequest;
import com.atypon.nosql.response.DatabaseResponse;
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
        Collection<String> databases = databasesManager.getDatabasesNames();
        Collection<Map<String, Object>> formattedResult = formatResult();
        return DatabaseResponse.builder()
                .message("Found [" + databases.size() + "] databases")
                .result(formattedResult)
                .build();
    }

    private List<Map<String, Object>> formatResult() {
        return databasesManager.getDatabasesNames().stream()
                .map(databaseName -> Map.of("database", (Object) databaseName))
                .toList();
    }
}

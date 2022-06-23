package com.atypon.nosql.requesthandlers.operationhandler;

import com.atypon.nosql.DatabasesManager;
import com.atypon.nosql.databaserequest.DatabaseOperation;
import com.atypon.nosql.databaserequest.DatabaseRequest;
import com.atypon.nosql.databaseresponse.DatabaseResponse;
import com.atypon.nosql.requesthandlers.DatabaseRequestHandler;
import com.atypon.nosql.requesthandlers.StorageHandler;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

@Component
public class GetCollectionsHandler implements DatabaseRequestHandler {

    private final DatabasesManager databasesManager;

    public GetCollectionsHandler(StorageHandler storageHandler, DatabasesManager databasesManager) {
        this.databasesManager = databasesManager;
        storageHandler.registerOperationHandler(DatabaseOperation.GET_COLLECTIONS, this);
    }

    @Override
    public DatabaseResponse handle(DatabaseRequest request) {
        Collection<String> result = databasesManager.getCollectionsNames(request.database());
        Collection<Map<String, Object>> collectionsNames = result.stream()
                .map(databaseName -> Map.of("collection", (Object) databaseName))
                .toList();
        return DatabaseResponse.createDatabaseResponse(
                String.format("Found [%d] collections", collectionsNames.size()),
                collectionsNames
        );
    }
}

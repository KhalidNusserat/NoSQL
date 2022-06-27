package com.atypon.nosql.requesthandlers.operationhandler;

import com.atypon.nosql.DatabasesManager;
import com.atypon.nosql.request.DatabaseOperation;
import com.atypon.nosql.request.DatabaseRequest;
import com.atypon.nosql.requesthandlers.DatabaseRequestHandler;
import com.atypon.nosql.requesthandlers.StorageHandler;
import com.atypon.nosql.response.DatabaseResponse;
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
        Collection<String> collections = databasesManager.getDatabase(request.database()).getCollectionsNames();
        Collection<Map<String, Object>> formattedResult = formatResults(collections);
        return DatabaseResponse.builder()
                .message("Found [" + collections.size() + "] collections")
                .result(formattedResult)
                .build();
    }

    private Collection<Map<String, Object>> formatResults(Collection<String> result) {
        return result.stream()
                .map(databaseName -> Map.of("collection", (Object) databaseName))
                .toList();
    }
}

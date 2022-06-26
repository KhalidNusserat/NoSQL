package com.atypon.nosql.requesthandlers.operationhandler;

import com.atypon.nosql.DatabasesManager;
import com.atypon.nosql.document.Document;
import com.atypon.nosql.request.DatabaseOperation;
import com.atypon.nosql.request.DatabaseRequest;
import com.atypon.nosql.response.DatabaseResponse;
import com.atypon.nosql.requesthandlers.DatabaseRequestHandler;
import com.atypon.nosql.requesthandlers.StorageHandler;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

@Component
public class GetIndexesHandler implements DatabaseRequestHandler {

    private final DatabasesManager databasesManager;

    public GetIndexesHandler(StorageHandler storageHandler, DatabasesManager databasesManager) {
        this.databasesManager = databasesManager;
        storageHandler.registerOperationHandler(DatabaseOperation.GET_INDEXES, this);
    }

    @Override
    public DatabaseResponse handle(DatabaseRequest request) {
        Collection<Map<String, Object>> indexes = databasesManager.getDatabase(request.database())
                .getCollection(request.collection())
                .getIndexes()
                .stream()
                .map(Document::toMap)
                .toList();
        return DatabaseResponse.builder()
                .message("Found [" + indexes.size() + "] indexes")
                .result(indexes)
                .build();
    }
}

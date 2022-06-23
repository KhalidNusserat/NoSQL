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
public class GetIndexesHandler implements DatabaseRequestHandler {

    private final DatabasesManager databasesManager;

    public GetIndexesHandler(StorageHandler storageHandler, DatabasesManager databasesManager) {
        this.databasesManager = databasesManager;
        storageHandler.registerOperationHandler(DatabaseOperation.GET_INDEXES, this);
    }

    @Override
    public DatabaseResponse handle(DatabaseRequest request) {
        Collection<Map<String, Object>> indexes = databasesManager.getCollectionIndexes(
                request.database(),
                request.collection()
        );
        return DatabaseResponse.createDatabaseResponse(
                String.format("Found [%d] indexes", indexes.size()),
                indexes
        );
    }
}

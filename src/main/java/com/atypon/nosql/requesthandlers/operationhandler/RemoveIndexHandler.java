package com.atypon.nosql.requesthandlers.operationhandler;

import com.atypon.nosql.DatabasesManager;
import com.atypon.nosql.request.DatabaseOperation;
import com.atypon.nosql.request.DatabaseRequest;
import com.atypon.nosql.request.Payload;
import com.atypon.nosql.requesthandlers.DatabaseRequestHandler;
import com.atypon.nosql.requesthandlers.StorageHandler;
import com.atypon.nosql.response.DatabaseResponse;
import org.springframework.stereotype.Component;

@Component
public class RemoveIndexHandler implements DatabaseRequestHandler {

    private final DatabasesManager databasesManager;

    public RemoveIndexHandler(StorageHandler storageHandler, DatabasesManager databasesManager) {
        this.databasesManager = databasesManager;
        storageHandler.registerOperationHandler(DatabaseOperation.REMOVE_INDEX, this);
    }

    @Override
    public DatabaseResponse handle(DatabaseRequest request) {
        Payload payload = request.payload();
        databasesManager.getDatabase(request.database())
                .getCollection(request.collection())
                .removeIndex(payload.index());
        String message = String.format(
                "Removed an index from the collection <%s/%s>",
                request.database(),
                request.collection()
        );
        return DatabaseResponse.builder()
                .message(message)
                .build();
    }
}

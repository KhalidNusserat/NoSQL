package com.atypon.nosql.requesthandlers.operationhandler;

import com.atypon.nosql.databaserequest.DatabaseOperation;
import com.atypon.nosql.databaserequest.DatabaseRequest;
import com.atypon.nosql.databaserequest.Payload;
import com.atypon.nosql.databaseresponse.DatabaseResponse;
import com.atypon.nosql.requesthandlers.DatabaseRequestHandler;
import com.atypon.nosql.requesthandlers.StorageHandler;
import com.atypon.nosql.DatabasesManager;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
public class ReadDocumentsHandler implements DatabaseRequestHandler {

    private final DatabasesManager databasesManager;

    public ReadDocumentsHandler(StorageHandler storageHandler, DatabasesManager databasesManager) {
        this.databasesManager = databasesManager;
        storageHandler.registerOperationHandler(DatabaseOperation.READ_DOCUMENTS, this);
    }

    @Override
    public DatabaseResponse handle(DatabaseRequest request) {
        Payload payload = request.payload();
        Collection<Map<String, Object>> documents = databasesManager.getDocuments(
                request.database(),
                request.collection(),
                payload.criteria()
        );
        return DatabaseResponse.createDatabaseResponse(
                String.format("Found [%d] documents", documents.size()),
                documents
        );
    }
}

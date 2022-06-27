package com.atypon.nosql.requesthandlers;

import com.atypon.nosql.request.DatabaseOperation;
import com.atypon.nosql.request.DatabaseRequest;
import com.atypon.nosql.response.DatabaseResponse;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component("defaultHandler")
public class StorageHandler implements DatabaseRequestHandler {

    private final Map<DatabaseOperation, DatabaseRequestHandler> operationsHandlers = new HashMap<>();

    @Override
    public DatabaseResponse handle(DatabaseRequest request) {
        return operationsHandlers.get(request.operation()).handle(request);
    }

    public void registerOperationHandler(DatabaseOperation operation, DatabaseRequestHandler requestHandler) {
        operationsHandlers.put(operation, requestHandler);
    }
}

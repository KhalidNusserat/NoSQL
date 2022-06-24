package com.atypon.nosql.requesthandlers;

import com.atypon.nosql.request.DatabaseOperation;
import com.atypon.nosql.request.DatabaseRequest;
import com.atypon.nosql.response.DatabaseResponse;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component("defaultHandler")
public class StorageHandler implements DatabaseRequestHandler {

    private final Map<DatabaseOperation, DatabaseRequestHandler> operationsHandlers = new ConcurrentHashMap<>();

    private final SynchronizationHandler synchronizationHandler;

    public StorageHandler(SynchronizationHandler synchronizationHandler) {
        this.synchronizationHandler = synchronizationHandler;
    }

    @Override
    public DatabaseResponse handle(DatabaseRequest request) {
        DatabaseOperation operation = request.operation();
        DatabaseResponse response = operationsHandlers.get(operation).handle(request);
        synchronizationHandler.handle(request);
        return response;
    }

    public void registerOperationHandler(DatabaseOperation operation, DatabaseRequestHandler handler) {
        operationsHandlers.put(operation, handler);
    }
}

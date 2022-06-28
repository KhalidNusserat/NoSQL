package com.atypon.nosql.request.handlers;

import com.atypon.nosql.request.DatabaseRequest;
import com.atypon.nosql.request.filters.DatabaseRequestsFiltersManager;
import com.atypon.nosql.response.DatabaseResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("defaultHandler")
public class DefaultHandler implements DatabaseRequestHandler {

    private final DatabaseRequestHandler operationsHandler;

    private final DatabaseRequestHandler synchronizationHandler;

    private final DatabaseRequestsFiltersManager filtersManager;

    public DefaultHandler(
            @Qualifier("operationsHandler") DatabaseRequestHandler operationsHandler,
            @Qualifier("syncHandler") DatabaseRequestHandler synchronizationHandler,
            DatabaseRequestsFiltersManager filtersManager) {
        this.operationsHandler = operationsHandler;
        this.synchronizationHandler = synchronizationHandler;
        this.filtersManager = filtersManager;
    }

    @Override
    public DatabaseResponse handle(DatabaseRequest request) {
        request = filtersManager.applyOn(request);
        DatabaseResponse response = operationsHandler.handle(request);
        synchronizationHandler.handle(request);
        return response;
    }
}

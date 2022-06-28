package com.atypon.nosql.request.filters;

import com.atypon.nosql.request.DatabaseRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DatabaseRequestsFiltersManager {

    private final List<DatabaseRequestFilter> filters;

    public DatabaseRequestsFiltersManager(List<DatabaseRequestFilter> filters) {
        this.filters = filters;
    }

    public DatabaseRequest applyOn(DatabaseRequest request) {
        for (DatabaseRequestFilter filter : filters) {
            request = filter.applyOn(request);
        }
        return request;
    }
}

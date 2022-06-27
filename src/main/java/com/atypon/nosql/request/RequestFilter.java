package com.atypon.nosql.request;

import com.atypon.nosql.request.DatabaseRequest;

public interface RequestFilter {
    DatabaseRequest filter(DatabaseRequest request);
}

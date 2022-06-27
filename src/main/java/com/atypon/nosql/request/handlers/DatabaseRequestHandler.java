package com.atypon.nosql.request.handlers;

import com.atypon.nosql.request.DatabaseRequest;
import com.atypon.nosql.response.DatabaseResponse;

public interface DatabaseRequestHandler {
    DatabaseResponse handle(DatabaseRequest request);
}

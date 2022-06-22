package com.atypon.nosql;

import com.atypon.nosql.databaserequest.DatabaseRequest;

public interface DatabaseRequestHandler {
    void handle(DatabaseRequest request);
}

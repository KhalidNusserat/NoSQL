package com.atypon.nosql.requesthandlers;

import com.atypon.nosql.databaserequest.DatabaseRequest;

public interface DatabaseRequestHandler {
    void handle(DatabaseRequest request);
}

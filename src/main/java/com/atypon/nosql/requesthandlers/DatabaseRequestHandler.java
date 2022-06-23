package com.atypon.nosql.requesthandlers;

import com.atypon.nosql.databaserequest.DatabaseRequest;
import com.atypon.nosql.databaseresponse.DatabaseResponse;

public interface DatabaseRequestHandler {
    DatabaseResponse handle(DatabaseRequest request);
}

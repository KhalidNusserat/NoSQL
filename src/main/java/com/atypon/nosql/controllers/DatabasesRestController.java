package com.atypon.nosql.controllers;

import com.atypon.nosql.request.DatabaseOperation;
import com.atypon.nosql.request.DatabaseRequest;
import com.atypon.nosql.request.DatabaseRequestFormatter;
import com.atypon.nosql.response.DatabaseResponse;
import com.atypon.nosql.requesthandlers.DatabaseRequestHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class DatabasesRestController {

    private final DatabaseRequestHandler requestHandler;

    private final DatabaseRequestFormatter requestFormatter;

    public DatabasesRestController(
            @Qualifier("defaultHandler") DatabaseRequestHandler requestHandler,
            DatabaseRequestFormatter requestFormatter
    ) {
        this.requestHandler = requestHandler;
        this.requestFormatter = requestFormatter;
    }

    @GetMapping("/databases")
    public ResponseEntity<DatabaseResponse> getDatabases() {
        DatabaseRequest request = DatabaseRequest.builder()
                .operation(DatabaseOperation.GET_DATABASES)
                .build();
        request = requestFormatter.format(request);
        return ResponseEntity.ok(requestHandler.handle(request));
    }

    @PostMapping("/databases/{database}")
    public ResponseEntity<DatabaseResponse> createDatabase(@PathVariable String database) {
        DatabaseRequest request = DatabaseRequest.builder()
                .database(database)
                .operation(DatabaseOperation.CREATE_DATABASE)
                .build();
        request = requestFormatter.format(request);
        return ResponseEntity.ok(requestHandler.handle(request));
    }

    @DeleteMapping("/databases/{database}")
    public ResponseEntity<DatabaseResponse> removeDatabase(@PathVariable String database) {
        DatabaseRequest request = DatabaseRequest.builder()
                .database(database)
                .operation(DatabaseOperation.REMOVE_DATABASE)
                .build();
        request = requestFormatter.format(request);
        return ResponseEntity.ok(requestHandler.handle(request));
    }
}

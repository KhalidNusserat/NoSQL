package com.atypon.nosql.controllers;

import com.atypon.nosql.databaserequest.DatabaseOperation;
import com.atypon.nosql.databaserequest.DatabaseRequest;
import com.atypon.nosql.databaserequest.DatabaseRequestFormatter;
import com.atypon.nosql.databaserequest.Payload;
import com.atypon.nosql.databaseresponse.DatabaseResponse;
import com.atypon.nosql.requesthandlers.DatabaseRequestHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class IndexesRestController {

    private final DatabaseRequestHandler requestHandler;

    private final DatabaseRequestFormatter requestFormatter;

    public IndexesRestController(
            @Qualifier("defaultHandler") DatabaseRequestHandler requestHandler,
            DatabaseRequestFormatter requestFormatter
    ) {
        this.requestHandler = requestHandler;
        this.requestFormatter = requestFormatter;
    }

    @GetMapping("/databases/{database}/collections/{collection}/indexes")
    public ResponseEntity<DatabaseResponse> getIndexes(
            @PathVariable String database,
            @PathVariable String collection
    ) {
        DatabaseRequest request = DatabaseRequest.builder()
                .setDatabase(database)
                .setCollection(collection)
                .setOperation(DatabaseOperation.GET_INDEXES)
                .createDocumentRequest();
        request = requestFormatter.format(request);
        return ResponseEntity.ok(requestHandler.handle(request));
    }

    @PostMapping("/databases/{database}/collections/{collection}/indexes")
    public ResponseEntity<DatabaseResponse> createIndex(
            @PathVariable String database,
            @PathVariable String collection,
            @RequestBody Payload payload
    ) {
        DatabaseRequest request = DatabaseRequest.builder()
                .setDatabase(database)
                .setCollection(collection)
                .setOperation(DatabaseOperation.CREATE_INDEX)
                .setPayload(payload)
                .createDocumentRequest();
        request = requestFormatter.format(request);
        return ResponseEntity.ok(requestHandler.handle(request));
    }

    @DeleteMapping("/databases/{database}/collections/{collection}/indexes")
    public ResponseEntity<DatabaseResponse> removeIndex(
            @PathVariable String database,
            @PathVariable String collection,
            @RequestBody Payload payload
    ) {
        DatabaseRequest request = DatabaseRequest.builder()
                .setDatabase(database)
                .setCollection(collection)
                .setOperation(DatabaseOperation.REMOVE_INDEX)
                .setPayload(payload)
                .createDocumentRequest();
        request = requestFormatter.format(request);
        return ResponseEntity.ok(requestHandler.handle(request));
    }
}

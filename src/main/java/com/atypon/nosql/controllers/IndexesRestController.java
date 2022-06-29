package com.atypon.nosql.controllers;

import com.atypon.nosql.request.DatabaseOperation;
import com.atypon.nosql.request.DatabaseRequest;
import com.atypon.nosql.request.Payload;
import com.atypon.nosql.request.handlers.DatabaseRequestHandler;
import com.atypon.nosql.response.DatabaseResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class IndexesRestController {

    private final DatabaseRequestHandler requestHandler;

    public IndexesRestController(
            @Qualifier("defaultHandler") DatabaseRequestHandler requestHandler
    ) {
        this.requestHandler = requestHandler;
    }

    @GetMapping("/databases/{database}/collections/{collection}/indexes")
    public ResponseEntity<DatabaseResponse> getIndexes(
            @PathVariable String database,
            @PathVariable String collection
    ) {
        DatabaseRequest request = DatabaseRequest.builder()
                .database(database)
                .collection(collection)
                .operation(DatabaseOperation.GET_INDEXES)
                .build();
        return ResponseEntity.ok(requestHandler.handle(request));
    }

    @PostMapping("/databases/{database}/collections/{collection}/indexes")
    public ResponseEntity<DatabaseResponse> createIndex(
            @PathVariable String database,
            @PathVariable String collection,
            @RequestBody Payload payload
    ) {
        DatabaseRequest request = DatabaseRequest.builder()
                .database(database)
                .collection(collection)
                .operation(DatabaseOperation.CREATE_INDEX)
                .payload(payload)
                .build();
        return ResponseEntity.ok(requestHandler.handle(request));
    }

    @PostMapping("/databases/{database}/collections/{collection}/indexes/deleted-indexes")
    public ResponseEntity<DatabaseResponse> removeIndex(
            @PathVariable String database,
            @PathVariable String collection,
            @RequestBody Payload payload
    ) {
        DatabaseRequest request = DatabaseRequest.builder()
                .database(database)
                .collection(collection)
                .operation(DatabaseOperation.REMOVE_INDEX)
                .payload(payload)
                .build();
        return ResponseEntity.ok(requestHandler.handle(request));
    }
}

package com.atypon.nosql.controllers;

import com.atypon.nosql.request.DatabaseOperation;
import com.atypon.nosql.request.DatabaseRequest;
import com.atypon.nosql.request.Payload;
import com.atypon.nosql.requesthandlers.DatabaseRequestHandler;
import com.atypon.nosql.response.DatabaseResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class CollectionsRestController {

    private final DatabaseRequestHandler requestHandler;

    public CollectionsRestController(
            @Qualifier("defaultHandler") DatabaseRequestHandler requestHandler
    ) {
        this.requestHandler = requestHandler;
    }

    @GetMapping("/databases/{database}/collections")
    public ResponseEntity<DatabaseResponse> getCollections(@PathVariable String database) {
        DatabaseRequest request = DatabaseRequest.builder()
                .database(database)
                .operation(DatabaseOperation.GET_COLLECTIONS)
                .build();
        return ResponseEntity.ok(requestHandler.handle(request));
    }

    @PostMapping("/databases/{database}/collections/{collection}")
    public ResponseEntity<DatabaseResponse> createCollection(
            @PathVariable String database,
            @PathVariable String collection,
            @RequestBody Payload payload
    ) {
        DatabaseRequest request = DatabaseRequest.builder()
                .database(database)
                .collection(collection)
                .operation(DatabaseOperation.CREATE_COLLECTION)
                .payload(payload)
                .build();
        return ResponseEntity.ok(requestHandler.handle(request));
    }

    @DeleteMapping("/databases/{database}/collections/{collection}")
    public ResponseEntity<DatabaseResponse> removeCollection(
            @PathVariable String database,
            @PathVariable String collection
    ) {
        DatabaseRequest request = DatabaseRequest.builder()
                .database(database)
                .collection(collection)
                .operation(DatabaseOperation.REMOVE_COLLECTION)
                .build();
        return ResponseEntity.ok(requestHandler.handle(request));
    }
}

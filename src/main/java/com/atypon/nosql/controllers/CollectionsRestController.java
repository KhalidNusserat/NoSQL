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
public class CollectionsRestController {

    private final DatabaseRequestHandler requestHandler;

    private final DatabaseRequestFormatter requestFormatter;

    public CollectionsRestController(
            @Qualifier("defaultHandler") DatabaseRequestHandler requestHandler,
            DatabaseRequestFormatter requestFormatter
    ) {
        this.requestHandler = requestHandler;
        this.requestFormatter = requestFormatter;
    }

    @GetMapping("/databases/{database}/collections")
    public ResponseEntity<DatabaseResponse> getCollections(@PathVariable String database) {
        DatabaseRequest request = DatabaseRequest.builder()
                .setDatabase(database)
                .setOperation(DatabaseOperation.GET_COLLECTIONS)
                .createDocumentRequest();
        request = requestFormatter.format(request);
        return ResponseEntity.ok(requestHandler.handle(request));
    }

    @PostMapping("/databases/{database}/collections/{collection}")
    public ResponseEntity<DatabaseResponse> createCollection(
            @PathVariable String database,
            @PathVariable String collection,
            @RequestBody Payload payload
    ) {
        DatabaseRequest request = DatabaseRequest.builder()
                .setDatabase(database)
                .setCollection(collection)
                .setOperation(DatabaseOperation.CREATE_COLLECTION)
                .setPayload(payload)
                .createDocumentRequest();
        request = requestFormatter.format(request);
        return ResponseEntity.ok(requestHandler.handle(request));
    }

    @DeleteMapping("/databases/{database}/collections/{collection}")
    public ResponseEntity<DatabaseResponse> removeCollection(
            @PathVariable String database,
            @PathVariable String collection
    ) {
        DatabaseRequest request = DatabaseRequest.builder()
                .setDatabase(database)
                .setCollection(collection)
                .setOperation(DatabaseOperation.REMOVE_COLLECTION)
                .createDocumentRequest();
        request = requestFormatter.format(request);
        return ResponseEntity.ok(requestHandler.handle(request));
    }
}

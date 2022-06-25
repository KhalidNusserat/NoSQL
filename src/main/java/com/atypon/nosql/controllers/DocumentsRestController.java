package com.atypon.nosql.controllers;

import com.atypon.nosql.request.DatabaseOperation;
import com.atypon.nosql.request.DatabaseRequest;
import com.atypon.nosql.request.DatabaseRequestFormatter;
import com.atypon.nosql.request.Payload;
import com.atypon.nosql.response.DatabaseResponse;
import com.atypon.nosql.requesthandlers.DatabaseRequestHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class DocumentsRestController {

    private final DatabaseRequestHandler requestHandler;

    private final DatabaseRequestFormatter requestFormatter;

    public DocumentsRestController(
            @Qualifier("defaultHandler") DatabaseRequestHandler requestHandler,
            DatabaseRequestFormatter requestFormatter
    ) {
        this.requestHandler = requestHandler;
        this.requestFormatter = requestFormatter;
    }

    @GetMapping("/databases/{database}/collections/{collection}/documents")
    public ResponseEntity<DatabaseResponse> readDocuments(
            @PathVariable String database,
            @PathVariable String collection,
            @RequestBody Payload payload
            ) {
        DatabaseRequest request = DatabaseRequest.builder()
                .database(database)
                .collection(collection)
                .operation(DatabaseOperation.READ_DOCUMENTS)
                .payload(payload)
                .build();
        request = requestFormatter.format(request);
        return ResponseEntity.ok(requestHandler.handle(request));
    }

    @PostMapping("/databases/{database}/collections/{collection}/documents")
    public ResponseEntity<DatabaseResponse> addDocuments(
            @PathVariable String database,
            @PathVariable String collection,
            @RequestBody Payload payload
    ) {
        DatabaseRequest request = DatabaseRequest.builder()
                .database(database)
                .collection(collection)
                .operation(DatabaseOperation.ADD_DOCUMENT)
                .payload(payload)
                .build();
        request = requestFormatter.format(request);
        return ResponseEntity.ok(requestHandler.handle(request));
    }

    @DeleteMapping("/databases/{database}/collections/{collection}/documents")
    public ResponseEntity<DatabaseResponse> removeDocuments(
            @PathVariable String database,
            @PathVariable String collection,
            @RequestBody Payload payload
    ) {
        DatabaseRequest request = DatabaseRequest.builder()
                .database(database)
                .collection(collection)
                .operation(DatabaseOperation.REMOVE_DOCUMENTS)
                .payload(payload)
                .build();
        request = requestFormatter.format(request);
        return ResponseEntity.ok(requestHandler.handle(request));
    }

    @PutMapping("/databases/{database}/collections/{collection}/documents")
    public ResponseEntity<DatabaseResponse> updateDocuments(
            @PathVariable String database,
            @PathVariable String collection,
            @RequestBody Payload payload
    ) {
        DatabaseRequest request = DatabaseRequest.builder()
                .database(database)
                .collection(collection)
                .operation(DatabaseOperation.UPDATE_DOCUMENTS)
                .payload(payload)
                .build();
        request = requestFormatter.format(request);
        return ResponseEntity.ok(requestHandler.handle(request));
    }
}

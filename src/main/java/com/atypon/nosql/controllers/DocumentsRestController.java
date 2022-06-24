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
                .setDatabase(database)
                .setCollection(collection)
                .setOperation(DatabaseOperation.READ_DOCUMENTS)
                .setPayload(payload)
                .createDocumentRequest();
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
                .setDatabase(database)
                .setCollection(collection)
                .setOperation(DatabaseOperation.ADD_DOCUMENT)
                .setPayload(payload)
                .createDocumentRequest();
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
                .setDatabase(database)
                .setCollection(collection)
                .setOperation(DatabaseOperation.REMOVE_DOCUMENTS)
                .setPayload(payload)
                .createDocumentRequest();
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
                .setDatabase(database)
                .setCollection(collection)
                .setOperation(DatabaseOperation.UPDATE_DOCUMENTS)
                .setPayload(payload)
                .createDocumentRequest();
        request = requestFormatter.format(request);
        return ResponseEntity.ok(requestHandler.handle(request));
    }
}

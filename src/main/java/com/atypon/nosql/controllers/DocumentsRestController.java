package com.atypon.nosql.controllers;

import com.atypon.nosql.cache.Cache;
import com.atypon.nosql.request.DatabaseOperation;
import com.atypon.nosql.request.DatabaseRequest;
import com.atypon.nosql.request.Payload;
import com.atypon.nosql.request.handlers.DatabaseRequestHandler;
import com.atypon.nosql.response.DatabaseResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Random;

@RestController
public class DocumentsRestController {

    private final DatabaseRequestHandler requestHandler;

    private final Random random = new Random();

    private final Cache<String, DatabaseResponse> storedResultsCache;

    public DocumentsRestController(
            @Qualifier("defaultHandler") DatabaseRequestHandler requestHandler,
            Cache<String, DatabaseResponse> storedResultsCache) {
        this.requestHandler = requestHandler;
        this.storedResultsCache = storedResultsCache;
    }

    @PostMapping("/databases/{database}/collections/{collection}/documents/searches")
    public ResponseEntity<String> readDocuments(
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
        DatabaseResponse response = requestHandler.handle(request);
        String hash = Long.toString(random.nextLong());
        storedResultsCache.put(hash, response);
        return ResponseEntity.ok(hash);
    }

    @GetMapping("/databases/{database}/collections/{collection}/documents/searches/{id}")
    public ResponseEntity<DatabaseResponse> readDocuments(
            @PathVariable String database,
            @PathVariable String collection,
            @PathVariable String id
    ) {
        Optional<DatabaseResponse> response = storedResultsCache.get(id);
        if (response.isPresent()) {
            storedResultsCache.remove(id);
            return ResponseEntity.ok(response.get());
        } else {
            return ResponseEntity.badRequest().build();
        }
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
        return ResponseEntity.ok(requestHandler.handle(request));
    }
}

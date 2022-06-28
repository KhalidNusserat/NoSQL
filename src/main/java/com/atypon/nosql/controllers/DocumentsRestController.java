package com.atypon.nosql.controllers;

import com.atypon.nosql.cache.Cache;
import com.atypon.nosql.cache.LRUCache;
import com.atypon.nosql.request.DatabaseOperation;
import com.atypon.nosql.request.DatabaseRequest;
import com.atypon.nosql.request.Payload;
import com.atypon.nosql.request.handlers.DatabaseRequestHandler;
import com.atypon.nosql.response.DatabaseResponse;
import com.google.common.hash.Hashing;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class DocumentsRestController {

    private final DatabaseRequestHandler requestHandler;

    private final Random random = new Random();

    private final Cache<String, DatabaseResponse> storedResults = new LRUCache<>(10000);

    public DocumentsRestController(
            @Qualifier("defaultHandler") DatabaseRequestHandler requestHandler
    ) {
        this.requestHandler = requestHandler;
    }

    @PostMapping("/databases/{database}/collections/{collection}/documents/search")
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
        storedResults.put(hash, response);
        return ResponseEntity.ok(hash);
    }

    @GetMapping("/databases/{database}/collections/{collection}/documents/search/{id}")
    public ResponseEntity<DatabaseResponse> readDocuments(
            @PathVariable String database,
            @PathVariable String collection,
            @PathVariable String id
    ) {
        Optional<DatabaseResponse> response = storedResults.get(id);
        if (response.isPresent()) {
            storedResults.remove(id);
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

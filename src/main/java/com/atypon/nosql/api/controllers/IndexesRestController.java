package com.atypon.nosql.api.controllers;

import com.atypon.nosql.api.services.DatabasesService;
import com.atypon.nosql.synchronisation.SynchronisationService;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;

@RestController
public class IndexesRestController {
    private final DatabasesService databasesService;

    private final SynchronisationService synchronisationService;

    public IndexesRestController(
            DatabasesService databasesService,
            SynchronisationService synchronisationService) {
        this.databasesService = databasesService;
        this.synchronisationService = synchronisationService;
    }

    @GetMapping("/databases/{database}/collections/{collection}/indexes")
    public ResponseEntity<Collection<Map<String, Object>>> getIndexes(
            @PathVariable("database") String databaseName,
            @PathVariable("collection") String collectionName
    ) {
        return ResponseEntity.ok(databasesService.getCollectionIndexes(databaseName, collectionName));
    }

    @PostMapping("/databases/{database}/collections/{collection}/indexes")
    public ResponseEntity<String> createIndex(
            @PathVariable("database") String databaseName,
            @PathVariable("collection") String collectionName,
            @RequestBody Map<String, Object> indexMap
    ) {
        databasesService.createIndex(databaseName, collectionName, indexMap);
        synchronisationService
                .method(HttpMethod.POST)
                .requestBody(indexMap)
                .url("/databases/{database}/collections/{collection}/indexes")
                .parameters(databaseName, collectionName)
                .synchronise();
        return ResponseEntity.ok("Created [1] indexDocumentString");
    }

    @DeleteMapping("/databases/{database}/collections/{collection}/indexes")
    public ResponseEntity<String> removeIndex(
            @PathVariable("database") String databaseName,
            @PathVariable("collection") String collectionName,
            @RequestBody Map<String, Object> indexMap
    ) {
        databasesService.removeIndex(databaseName, collectionName, indexMap);
        synchronisationService
                .method(HttpMethod.DELETE)
                .requestBody(indexMap)
                .url("/databases/{database}/collections/{collection}/indexes")
                .parameters(databaseName, collectionName)
                .synchronise();
        return ResponseEntity.ok("Removed [1] index");
    }
}

package com.atypon.nosql.controllers;

import com.atypon.nosql.services.DatabasesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;

@RestController
public class IndexesRestController {
    private final DatabasesService databasesService;

    public IndexesRestController(DatabasesService databasesService) {
        this.databasesService = databasesService;
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
        return ResponseEntity.ok("Created [1] indexDocumentString");
    }

    @DeleteMapping("/databases/{database}/collections/{collection}/indexes")
    public ResponseEntity<String> removeIndex(
            @PathVariable("database") String databaseName,
            @PathVariable("collection") String collectionName,
            @RequestBody Map<String, Object> indexMap
    ) {
        databasesService.removeIndex(databaseName, collectionName, indexMap);
        return ResponseEntity.ok("Removed [1] index");
    }
}

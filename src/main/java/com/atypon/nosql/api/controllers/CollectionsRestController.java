package com.atypon.nosql.api.controllers;

import com.atypon.nosql.api.services.DatabasesService;
import com.atypon.nosql.synchronisation.SynchronisationService;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;

@RestController
public class CollectionsRestController {
    private final DatabasesService databasesService;

    private final SynchronisationService synchronisationService;

    public CollectionsRestController(
            DatabasesService databasesService,
            SynchronisationService synchronisationService) {
        this.databasesService = databasesService;
        this.synchronisationService = synchronisationService;
    }

    @GetMapping("/databases/{database}/collections")
    public ResponseEntity<Collection<String>> getAllCollections(@PathVariable("database") String databaseName) {
        return ResponseEntity.ok(databasesService.getCollectionsNames(databaseName));
    }

    @GetMapping("/databases/{database}/collections/{collection}/schema")
    public ResponseEntity<Map<String, Object>> getSchema(
            @PathVariable("database") String databaseName,
            @PathVariable("collection") String collectionName) {
        return ResponseEntity.ok(databasesService.getDocumentsCollectionSchema(databaseName, collectionName));
    }

    @PostMapping("/databases/{database}/collections/{collection}")
    public ResponseEntity<String> createCollection(
            @PathVariable("database") String databaseName,
            @PathVariable("collection") String collectionName,
            @RequestBody Map<String, Object> schemaMap
    ) {
        databasesService.createDocumentsCollection(databaseName, collectionName, schemaMap);
        synchronisationService
                .method(HttpMethod.POST)
                .requestBody(schemaMap)
                .url("/databases/{database}/collections/{collection}")
                .parameters(databaseName, collectionName)
                .synchronise();
        return ResponseEntity.ok("Created collection: " + databaseName + "/" + collectionName);
    }

    @DeleteMapping("/databases/{database}/collections/{collection}")
    public ResponseEntity<String> removeCollection(
            @PathVariable("database") String databaseName,
            @PathVariable("collection") String collectionName
    ) {
        databasesService.removeDocumentsCollection(databaseName, collectionName);
        synchronisationService
                .method(HttpMethod.DELETE)
                .url("/databases/{database}/collections/{collection}")
                .parameters(databaseName, collectionName)
                .synchronise();
        return ResponseEntity.ok("Deleted collection: " + collectionName);
    }
}

package com.atypon.nosql.api.controllers;

import com.atypon.nosql.api.services.DatabasesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
public class CollectionsRestController {
    private final DatabasesService databasesService;

    public CollectionsRestController(DatabasesService databasesService) {
        this.databasesService = databasesService;
    }

    @GetMapping("/databases/{database}/collections")
    public ResponseEntity<Collection<String>> getAllCollections(@PathVariable("database") String database) {
        return ResponseEntity.ok(databasesService.get(database).getCollectionsNames());
    }

    @PostMapping("/databases/{database}/collections/{collection}")
    public ResponseEntity<String> createCollection(
            @PathVariable("database") String database,
            @PathVariable("collection") String collection,
            @RequestBody String schema
    ) {
        databasesService.get(database).createCollection(collection, schema);
        return ResponseEntity.ok("Created collection: " + database + "/" + collection);
    }

    @DeleteMapping("/databases/{database}/collections/{collection}")
    public ResponseEntity<String> removeCollection(
            @PathVariable("database") String database,
            @PathVariable("collection") String collection
    ) {
        databasesService.get(database).removeCollection(collection);
        return ResponseEntity.ok("Deleted collection: " + collection);
    }
}

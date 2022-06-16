package com.atypon.nosql;

import com.atypon.nosql.database.DatabasesManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
public class CollectionsRestController {
    private final DatabasesManager<?> databasesManager;

    public CollectionsRestController(DatabasesManager<?> databasesManager) {
        this.databasesManager = databasesManager;
    }

    @GetMapping("/databases/{database}/collections")
    public ResponseEntity<Collection<String>> getAllCollections(@PathVariable("database") String database) {
        return ResponseEntity.ok(databasesManager.get(database).getCollectionsNames());
    }

    @PostMapping("/databases/{database}/collections/{collection}")
    public ResponseEntity<String> createCollection(
            @PathVariable("database") String database,
            @PathVariable("collection") String collection,
            @RequestBody String schema
    ) {
        databasesManager.get(database).createCollection(collection, schema);
        return ResponseEntity.ok("Created collection: " + database + "/" + collection);
    }

    @DeleteMapping("/databases/{database}/collections/{collection}")
    public ResponseEntity<String> removeCollection(
            @PathVariable("database") String database,
            @PathVariable("collection") String collection
    ) {
        databasesManager.get(database).removeCollection(collection);
        return ResponseEntity.ok("Deleted collection: " + collection);
    }
}

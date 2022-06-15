package com.atypon.nosql;

import com.atypon.nosql.database.DatabasesManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
public class CollectionsRestController {
    private final DatabasesManager databasesManager;

    public CollectionsRestController(DatabasesManager databasesManager) {
        this.databasesManager = databasesManager;
    }

    private void checkDatabaseExists(String database) {
        if (!databasesManager.contains(database)) {
            throw new NoSuchDatabaseException(database);
        }
    }

    @GetMapping("/databases/{database}/collections")
    public ResponseEntity<Collection<String>> getAllCollections(@PathVariable("database") String database) {
        checkDatabaseExists(database);
        return ResponseEntity.ok(databasesManager.get(database).getCollectionsNames());
    }

    @PostMapping("/databases/{database}/collections/{collection}")
    public ResponseEntity<String> createCollection(
            @PathVariable("database") String database,
            @PathVariable("collection") String collection,
            @RequestBody String schema
    ) {
        checkDatabaseExists(database);
        databasesManager.get(database).createCollection(collection, schema);
        return ResponseEntity.ok("Created collection: " + database + "/" + collection);
    }

    @DeleteMapping("/databases/{database}/collections/{collection}")
    public ResponseEntity<String> removeCollection(
            @PathVariable("database") String database,
            @PathVariable("collection") String collection
    ) {
        checkDatabaseExists(database);
        databasesManager.remove(database);
        return ResponseEntity.ok("Deleted collection: " + collection);
    }
}

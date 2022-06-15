package com.atypon.nosql;

import com.atypon.nosql.database.DatabasesManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;

@RestController
public class IndexesRestController {
    private final DatabasesManager databasesManager;

    public IndexesRestController(DatabasesManager databasesManager) {
        this.databasesManager = databasesManager;
    }

    private void checkDatabaseExists(String database) {
        if (!databasesManager.contains(database)) {
            throw new NoSuchDatabaseException(database);
        }
    }

    @GetMapping("/databases/{database}/collections/{collection}/indexes")
    public ResponseEntity<Collection<Map<String, Object>>> getIndexes(
            @PathVariable("database") String database,
            @PathVariable("collection") String collection
    ) {
        checkDatabaseExists(database);
        return ResponseEntity.ok(databasesManager.get(database).getCollectionIndexes(collection));
    }

    @PostMapping("/databases/{database}/collection/{collection}/indexes")
    public ResponseEntity<String> createIndex(
            @PathVariable("database") String database,
            @PathVariable("collection") String collection,
            @RequestBody String index
    ) {
        checkDatabaseExists(database);
        databasesManager.get(database).createIndex(collection, index);
        return ResponseEntity.ok("Created [1] index");
    }

    @DeleteMapping("/databases/{database}/collection/{collection}/indexes")
    public ResponseEntity<String> deleteIndex(
            @PathVariable("database") String database,
            @PathVariable("collection") String collection,
            @RequestBody String matchIndex
    ) {
        checkDatabaseExists(database);
        databasesManager.get(database).deleteIndex(collection, matchIndex);
        return ResponseEntity.ok("Deleted [1] index");
    }
}

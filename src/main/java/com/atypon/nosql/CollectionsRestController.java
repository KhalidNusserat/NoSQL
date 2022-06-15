package com.atypon.nosql;

import com.atypon.nosql.database.Database;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;

@RestController
public class CollectionsRestController {
    private final Map<String, Database> databases;

    public CollectionsRestController(Map<String, Database> databases) {
        this.databases = databases;
    }

    private void checkDatabaseExists(String database) {
        if (!databases.containsKey(database)) {
            throw new NoSuchDatabaseException(database);
        }
    }

    @GetMapping("/databases/{database}/collections")
    public ResponseEntity<Collection<String>> getAllCollections(@PathVariable("database") String database) {
        checkDatabaseExists(database);
        return ResponseEntity.ok(databases.get(database).getCollectionsNames());
    }

    @PostMapping("/databases/{database}/collections/{collection}")
    public ResponseEntity<String> createCollection(
            @PathVariable("database") String database,
            @PathVariable("collection") String collection,
            @RequestBody String schema
    ) {
        checkDatabaseExists(database);
        databases.get(database).createCollection(collection, schema);
        return ResponseEntity.ok("Created collection: " + database + "/" + collection);
    }
}

package com.atypon.nosql;

import com.atypon.nosql.database.*;
import com.atypon.nosql.database.document.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.util.Collection;

@RestController
public class DatabasesRestController<T extends Document<?>> {
    private final DatabasesManager databasesManager;

    public DatabasesRestController(DatabasesManager databasesManager) {
        this.databasesManager = databasesManager;
    }

    @GetMapping("/databases")
    public ResponseEntity<Collection<String>> getDatabases() {
        return ResponseEntity.ok(databasesManager.getDatabasesNames());
    }

    @PostMapping("/databases/{database}")
    public ResponseEntity<String> createDatabase(@PathVariable("database") String database) {
        databasesManager.create(database);
        return ResponseEntity.ok("Database created: " + database);
    }

    @DeleteMapping("/databases/{database}")
    public ResponseEntity<String> deleteDatabase(@PathVariable("database") String database) {
        checkDatabaseExists(database);
        databasesManager.remove(database);
        return ResponseEntity.ok("Database removed: " + database);
    }

    private void checkDatabaseExists(String database) {
        if (!databasesManager.contains(database)) {
            throw new NoSuchDatabaseException(database);
        }
    }
}

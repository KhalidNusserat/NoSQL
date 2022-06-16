package com.atypon.nosql;

import com.atypon.nosql.database.DatabasesManager;
import com.atypon.nosql.database.gsondocument.GsonDocument;
import com.google.gson.Gson;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
public class DatabasesRestController {
    private final DatabasesManager<?> databasesManager;

    public DatabasesRestController(DatabasesManager<?> databasesManager) {
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
        databasesManager.remove(database);
        return ResponseEntity.ok("Database removed: " + database);
    }
}

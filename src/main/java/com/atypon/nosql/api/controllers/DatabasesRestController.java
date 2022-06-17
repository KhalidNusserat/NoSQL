package com.atypon.nosql.api.controllers;

import com.atypon.nosql.api.services.DatabasesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
public class DatabasesRestController {
    private final DatabasesService databasesService;

    public DatabasesRestController(DatabasesService databasesService) {
        this.databasesService = databasesService;
    }

    @GetMapping("/databases")
    public ResponseEntity<Collection<String>> getDatabases() {
        return ResponseEntity.ok(databasesService.getDatabasesNames());
    }

    @PostMapping("/databases/{database}")
    public ResponseEntity<String> createDatabase(@PathVariable("database") String database) {
        databasesService.create(database);
        return ResponseEntity.ok("Database created: " + database);
    }

    @DeleteMapping("/databases/{database}")
    public ResponseEntity<String> deleteDatabase(@PathVariable("database") String database) {
        databasesService.remove(database);
        return ResponseEntity.ok("Database removed: " + database);
    }
}

package com.atypon.nosql.api.controllers;

import com.atypon.nosql.api.services.DatabasesService;
import com.atypon.nosql.synchronisation.SynchronisationService;
import org.springframework.http.HttpMethod;
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
    public ResponseEntity<String> createDatabase(
            @PathVariable("database") String databaseName,
            @RequestHeader("Authorization") String auth) {
        System.out.println("AUTH: " + auth);
        databasesService.createDatabase(databaseName);
        return ResponseEntity.ok("Database created: " + databaseName);
    }

    @DeleteMapping("/databases/{database}")
    public ResponseEntity<String> deleteDatabase(@PathVariable("database") String databaseName) {
        databasesService.removeDatabase(databaseName);
        return ResponseEntity.ok("Database removed: " + databaseName);
    }
}

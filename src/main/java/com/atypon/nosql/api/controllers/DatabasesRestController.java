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

    private final SynchronisationService synchronisationService;

    public DatabasesRestController(
            DatabasesService databasesService,
            SynchronisationService synchronisationService) {
        this.databasesService = databasesService;
        this.synchronisationService = synchronisationService;
    }

    @GetMapping("/databases")
    public ResponseEntity<Collection<String>> getDatabases() {
        return ResponseEntity.ok(databasesService.getDatabasesNames());
    }

    @PostMapping("/databases/{database}")
    public ResponseEntity<String> createDatabase(@PathVariable("database") String databaseName) {
        databasesService.createDatabase(databaseName);
        synchronisationService
                .method(HttpMethod.POST)
                .url("/databases/{database}")
                .parameters(databaseName)
                .synchronise();
        return ResponseEntity.ok("Database created: " + databaseName);
    }

    @DeleteMapping("/databases/{database}")
    public ResponseEntity<String> deleteDatabase(@PathVariable("database") String databaseName) {
        databasesService.removeDatabase(databaseName);
        synchronisationService
                .method(HttpMethod.DELETE)
                .url("/databases/{database}")
                .parameters(databaseName)
                .synchronise();
        return ResponseEntity.ok("Database removed: " + databaseName);
    }
}

package com.atypon.nosql;

import com.atypon.nosql.database.DatabasesManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;

@RestController
public class DocumentsRestController {
    private final DatabasesManager databasesManager;

    public DocumentsRestController(DatabasesManager databasesManager) {
        this.databasesManager = databasesManager;
    }

    private void checkDatabaseExists(String database) {
        if (!databasesManager.contains(database)) {
            throw new NoSuchDatabaseException(database);
        }
    }

    @GetMapping("/databases/{database}/collections/{collection}/documents")
    public ResponseEntity<Collection<Map<String, Object>>> getDocumentsThatMatch(
            @PathVariable("database") String database,
            @PathVariable("collection") String collection,
            @RequestBody String matchDocument
    ) {
        checkDatabaseExists(database);
        return ResponseEntity.ok(databasesManager.get(database).readDocuments(collection, matchDocument));
    }

    @PostMapping("/databases/{database}/collections/{collection}/documents")
    public ResponseEntity<String> addDocument(
            @PathVariable("database") String database,
            @PathVariable("collection") String collection,
            @RequestBody String document
    ) {
        checkDatabaseExists(database);
        databasesManager.get(database).addDocument(collection, document);
        return ResponseEntity.ok("Added [1] document");
    }

    @DeleteMapping("/databases/{database}/collections/{collection}/documents")
    public ResponseEntity<String> deleteDocuments(
            @PathVariable("database") String database,
            @PathVariable("collection") String collection,
            @RequestBody String matchDocument
    ) {
        checkDatabaseExists(database);
        int deletedCount = databasesManager.get(database).deleteDocuments(collection, matchDocument);
        return ResponseEntity.ok("Deleted [" + deletedCount + "] documents");
    }
}

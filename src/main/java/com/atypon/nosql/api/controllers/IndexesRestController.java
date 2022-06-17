package com.atypon.nosql.api.controllers;

import com.atypon.nosql.api.services.DatabasesService;
import com.atypon.nosql.database.document.Document;
import com.atypon.nosql.database.document.DocumentFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;

@RestController
public class IndexesRestController {
    private final DatabasesService databasesService;

    private final DocumentFactory documentFactory;

    public IndexesRestController(DatabasesService databasesService, DocumentFactory documentFactory) {
        this.databasesService = databasesService;
        this.documentFactory = documentFactory;
    }

    @GetMapping("/databases/{database}/collections/{collection}/indexes")
    public ResponseEntity<Collection<Map<String, Object>>> getIndexes(
            @PathVariable("database") String databaseName,
            @PathVariable("collection") String collectionName
    ) {
        Collection<Document> result = databasesService.get(databaseName).get(collectionName).getIndexes();
        return ResponseEntity.ok(Document.getResultsAsMaps(result));
    }

    @PostMapping("/databases/{database}/collections/{collection}/indexes")
    public ResponseEntity<String> createIndex(
            @PathVariable("database") String databaseName,
            @PathVariable("collection") String collectionName,
            @RequestBody String indexDocumentString
    ) {
        Document indexDocument = documentFactory.createFromString(indexDocumentString);
        databasesService.get(databaseName).get(collectionName).createIndex(indexDocument);
        return ResponseEntity.ok("Created [1] indexDocumentString");
    }

    @DeleteMapping("/databases/{database}/collections/{collection}/indexes")
    public ResponseEntity<String> deleteIndex(
            @PathVariable("database") String databaseName,
            @PathVariable("collection") String collectionName,
            @RequestBody String indexDocumentString
    ) {
        Document indexDocument = documentFactory.createFromString(indexDocumentString);
        databasesService.get(databaseName).get(collectionName).deleteIndex(indexDocument);
        return ResponseEntity.ok("Deleted [1] index");
    }
}

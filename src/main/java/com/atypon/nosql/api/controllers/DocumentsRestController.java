package com.atypon.nosql.api.controllers;

import com.atypon.nosql.api.services.DatabasesService;
import com.atypon.nosql.synchronisation.SynchronisationService;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;

@RestController
public class DocumentsRestController {
    private final DatabasesService databasesService;

    private final SynchronisationService synchronisationService;

    public DocumentsRestController(
            DatabasesService databasesService,
            SynchronisationService synchronisationService) {
        this.databasesService = databasesService;
        this.synchronisationService = synchronisationService;
    }

    @GetMapping("/databases/{database}/collections/{collection}/documents")
    public ResponseEntity<Collection<Map<String, Object>>> getDocumentsThatMatch(
            @PathVariable("database") String databaseName,
            @PathVariable("collection") String collectionName,
            @RequestBody Map<String, Object> documentCriteriaMap
    ) {
        Collection<Map<String, Object>> result = databasesService.getDocuments(
                databaseName,
                collectionName,
                documentCriteriaMap
        );
        return ResponseEntity.ok(result);
    }

    @PostMapping("/databases/{database}/collections/{collection}/documents")
    public ResponseEntity<String> addDocument(
            @PathVariable("database") String databaseName,
            @PathVariable("collection") String collectionName,
            @RequestBody Map<String, Object> documentMap
    ) {
        databasesService.addDocument(databaseName, collectionName, documentMap);
        synchronisationService
                .method(HttpMethod.POST)
                .url("/databases/{database}/collections/{collection}/documents")
                .parameters(databaseName, collectionName)
                .synchronise();
        return ResponseEntity.ok("Added [1] document");
    }

    @DeleteMapping("/databases/{database}/collections/{collection}/documents")
    public ResponseEntity<String> deleteDocuments(
            @PathVariable("database") String databaseName,
            @PathVariable("collection") String collectionName,
            @RequestBody Map<String, Object> documentCriteriaMap
    ) {
        int deletedCount = databasesService.removeDocuments(databaseName, collectionName, documentCriteriaMap);
        synchronisationService
                .method(HttpMethod.DELETE)
                .url("/databases/{database}/collections/{collection}/documents")
                .parameters(databaseName, collectionName)
                .synchronise();
        return ResponseEntity.ok("Deleted [" + deletedCount + "] documents");
    }

    @PutMapping("/databases/{database}/collections/{collection}/documents/{documentId}")
    public ResponseEntity<String> updateDocument(
            @PathVariable("database") String databaseName,
            @PathVariable("collection") String collectionName,
            @PathVariable("documentId") String documentId,
            @RequestBody Map<String, Object> updatedDocumentMap
    ) {
        databasesService.updateDocument(databaseName, collectionName, documentId, updatedDocumentMap);
        synchronisationService
                .method(HttpMethod.PUT)
                .url("/databases/{database}/collections/{collection}/documents/{documentId}")
                .parameters(databaseName, collectionName, documentId)
                .synchronise();
        return ResponseEntity.ok("Updated [1] document");
    }
}

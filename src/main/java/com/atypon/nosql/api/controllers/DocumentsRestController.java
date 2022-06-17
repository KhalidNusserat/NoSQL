package com.atypon.nosql.api.controllers;

import com.atypon.nosql.database.Database;
import com.atypon.nosql.api.services.DatabasesService;
import com.atypon.nosql.database.collection.DocumentsCollection;
import com.atypon.nosql.database.document.Document;
import com.atypon.nosql.database.document.DocumentFactory;
import com.atypon.nosql.database.utils.DocumentUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;

@RestController
public class DocumentsRestController {
    private final DatabasesService databasesService;

    private final DocumentFactory documentFactory;

    public DocumentsRestController(DatabasesService databasesService, DocumentFactory documentFactory) {
        this.databasesService = databasesService;
        this.documentFactory = documentFactory;
    }

    @GetMapping("/databases/{database}/collections/{collection}/documents")
    public ResponseEntity<Collection<Map<String, Object>>> getDocumentsThatMatch(
            @PathVariable("database") String databaseName,
            @PathVariable("collection") String collectionName,
            @RequestBody String matchDocumentString
    ) {
        Document matchDocument = documentFactory.createFromString(matchDocumentString);
        Database database = databasesService.get(databaseName);
        DocumentsCollection documentsCollection = database.get(collectionName);
        Collection<Document> results = documentsCollection.getAllThatMatch(matchDocument);
        return ResponseEntity.ok(DocumentUtils.documentsToMaps(results));
    }

    @PostMapping("/databases/{database}/collections/{collection}/documents")
    public ResponseEntity<String> addDocument(
            @PathVariable("database") String databaseName,
            @PathVariable("collection") String collectionName,
            @RequestBody String documentString
    ) {
        Document documentWithoutId = documentFactory.createFromString(documentString);
        Document documentWithId = documentFactory.appendId(documentWithoutId);
        Database database = databasesService.get(databaseName);
        DocumentsCollection documentsCollection = database.get(collectionName);
        documentsCollection.addDocument(documentWithId);
        return ResponseEntity.ok("Added [1] document");
    }

    @DeleteMapping("/databases/{database}/collections/{collection}/documents")
    public ResponseEntity<String> deleteDocuments(
            @PathVariable("database") String databaseName,
            @PathVariable("collection") String collectionName,
            @RequestBody String matchDocumentString
    ) {
        Document matchDocument = documentFactory.createFromString(matchDocumentString);
        Database database = databasesService.get(databaseName);
        DocumentsCollection documentsCollection = database.get(collectionName);
        int deletedCount = documentsCollection.removeAllThatMatch(matchDocument);
        return ResponseEntity.ok("Deleted [" + deletedCount + "] documents");
    }
}

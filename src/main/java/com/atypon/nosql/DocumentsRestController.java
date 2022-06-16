package com.atypon.nosql;

import com.atypon.nosql.database.Database;
import com.atypon.nosql.database.DatabasesManager;
import com.atypon.nosql.database.collection.DocumentsCollection;
import com.atypon.nosql.database.document.Document;
import com.atypon.nosql.database.document.DocumentFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;

public abstract class DocumentsRestController {
    private final DatabasesManager databasesManager;

    private final DocumentFactory documentFactory;

    public DocumentsRestController(DatabasesManager databasesManager, DocumentFactory documentFactory) {
        this.databasesManager = databasesManager;
        this.documentFactory = documentFactory;
    }

    @GetMapping("/databases/{database}/collections/{collection}/documents")
    public ResponseEntity<Collection<Map<String, Object>>> getDocumentsThatMatch(
            @PathVariable("database") String databaseName,
            @PathVariable("collection") String collectionName,
            @RequestBody String matchDocumentString
    ) {
        Document matchDocument = documentFactory.createFromString(matchDocumentString);
        Database database = databasesManager.get(databaseName);
        DocumentsCollection documentsCollection = database.get(collectionName);
        Collection<Document> results = documentsCollection.getAllThatMatch(matchDocument);
        return ResponseEntity.ok(Document.getResultsAsMaps(results));
    }

    @PostMapping("/databases/{database}/collections/{collection}/documents")
    public ResponseEntity<String> addDocument(
            @PathVariable("database") String databaseName,
            @PathVariable("collection") String collectionName,
            @RequestBody String documentString
    ) {
        Document document = documentFactory.createFromString(documentString);
        Database database = databasesManager.get(databaseName);
        DocumentsCollection documentsCollection = database.get(collectionName);
        documentsCollection.addDocument(document);
        return ResponseEntity.ok("Added [1] document");
    }

    @DeleteMapping("/databases/{database}/collections/{collection}/documents")
    public ResponseEntity<String> deleteDocuments(
            @PathVariable("database") String databaseName,
            @PathVariable("collection") String collectionName,
            @RequestBody String matchDocumentString
    ) {
        Document matchDocument = documentFactory.createFromString(matchDocumentString);
        Database database = databasesManager.get(databaseName);
        DocumentsCollection documentsCollection = database.get(collectionName);
        int deletedCount = documentsCollection.removeAllThatMatch(matchDocument);
        return ResponseEntity.ok("Deleted [" + deletedCount + "] documents");
    }
}

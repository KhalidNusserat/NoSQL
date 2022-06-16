package com.atypon.nosql;

import com.atypon.nosql.database.Database;
import com.atypon.nosql.database.DatabasesManager;
import com.atypon.nosql.database.collection.DocumentsCollection;
import com.atypon.nosql.database.document.Document;
import com.atypon.nosql.database.document.DocumentGenerator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;

public abstract class DocumentsRestController<T extends Document> {
    private final DatabasesManager<T> databasesManager;

    private final DocumentGenerator<T> documentGenerator;

    public DocumentsRestController(DatabasesManager<T> databasesManager, DocumentGenerator<T> documentGenerator) {
        this.databasesManager = databasesManager;
        this.documentGenerator = documentGenerator;
    }

    @GetMapping("/databases/{database}/collections/{collection}/documents")
    public ResponseEntity<Collection<Map<String, Object>>> getDocumentsThatMatch(
            @PathVariable("database") String databaseName,
            @PathVariable("collection") String collectionName,
            @RequestBody String matchDocumentString
    ) {
        T matchDocument = documentGenerator.createFromString(matchDocumentString);
        Database<T> database = databasesManager.get(databaseName);
        DocumentsCollection<T> documentsCollection = database.get(collectionName);
        Collection<T> results = documentsCollection.getAllThatMatches(matchDocument);
        return ResponseEntity.ok(Document.getResultsAsMaps(results));
    }

    @PostMapping("/databases/{database}/collections/{collection}/documents")
    public ResponseEntity<String> addDocument(
            @PathVariable("database") String databaseName,
            @PathVariable("collection") String collectionName,
            @RequestBody String documentString
    ) {
        T document = documentGenerator.createFromString(documentString);
        Database<T> database = databasesManager.get(databaseName);
        DocumentsCollection<T> documentsCollection = database.get(collectionName);
        documentsCollection.addDocument(document);
        return ResponseEntity.ok("Added [1] document");
    }

    @DeleteMapping("/databases/{database}/collections/{collection}/documents")
    public ResponseEntity<String> deleteDocuments(
            @PathVariable("database") String databaseName,
            @PathVariable("collection") String collectionName,
            @RequestBody String matchDocumentString
    ) {
        T matchDocument = documentGenerator.createFromString(matchDocumentString);
        Database<T> database = databasesManager.get(databaseName);
        DocumentsCollection<T> documentsCollection = database.get(collectionName);
        int deletedCount = documentsCollection.removeAllThatMatches(matchDocument);
        return ResponseEntity.ok("Deleted [" + deletedCount + "] documents");
    }
}

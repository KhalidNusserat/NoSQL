package com.atypon.nosql;

import com.atypon.nosql.database.CollectionAlreadyExists;
import com.atypon.nosql.database.CollectionNotFoundException;
import com.atypon.nosql.database.Database;
import com.atypon.nosql.database.DocumentSchemaViolationException;
import com.atypon.nosql.database.collection.MultipleFilesMatchedException;
import com.atypon.nosql.database.collection.NoSuchDocumentException;
import com.atypon.nosql.database.collection.NoSuchIndexException;
import com.atypon.nosql.database.document.Document;
import com.atypon.nosql.database.document.InvalidDocumentSchema;
import com.atypon.nosql.database.gsondocument.FieldsDoNotMatchException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

@RestController
public class DatabaseRestController<T extends Document<?>> {
    private final Database database;

    protected DatabaseRestController(Database database) {
        this.database = database;
    }

    @GetMapping("/collections")
    public ResponseEntity<Collection<String>> getAllCollections() {
        return ResponseEntity.ok(database.getCollectionsNames());
    }

    @PostMapping("/collections/{collectionName}")
    public ResponseEntity<String> createNewCollection(
            @PathVariable("collectionName") String collectionName,
            @RequestBody String schemaString
    ) {
        try {
            database.createCollection(collectionName, schemaString);
            return ResponseEntity.ok().body("Created collection successfully: " + collectionName);
        } catch (InvalidDocumentSchema | CollectionAlreadyExists e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @DeleteMapping("/collections/{collectionName}")
    public ResponseEntity<String> deleteCollection(
            @PathVariable("collectionName") String collectionName
    ) {
        try {
            database.deleteCollection(collectionName);
            return ResponseEntity.ok().body("Deleted collection successfully: " + collectionName);
        } catch (CollectionNotFoundException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("collections/{collectionName}/documents")
    public ResponseEntity<Collection<Map<String, Object>>> getDocumentsThatMatch(
            @PathVariable("collectionName") String collectionName,
            @RequestBody String matchDocumentString
    ) {
        try {
            return ResponseEntity.ok(database.readDocuments(collectionName, matchDocumentString));
        } catch (FieldsDoNotMatchException | CollectionNotFoundException e) {
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("collections/{collectionName}/documents")
    public ResponseEntity<String> deleteDocumentsThatMatch(
            @PathVariable("collectionName") String collectionName,
            @RequestBody String matchDocumentString
    ) {
        try {
            database.deleteDocuments(collectionName, matchDocumentString);
            return ResponseEntity.ok().body("Deleted collection successfully: " + collectionName);
        } catch (FieldsDoNotMatchException | CollectionNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("An error occurred while deleting the documents");
        }
    }

    @PutMapping("collections/{collectionName}/documents/{documentID}")
    public ResponseEntity<String> updateDocument(
            @PathVariable("collectionName") String collectionName,
            @PathVariable("documentID") String documentID,
            @RequestBody String updatedDocumentString
    ) {
        try {
            database.updateDocument(collectionName, documentID, updatedDocumentString);
            return ResponseEntity.ok().body("Updated document successfully");
        } catch (MultipleFilesMatchedException | NoSuchDocumentException | DocumentSchemaViolationException
                | CollectionNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("An error occurred while updating the document");
        }
    }

    @PostMapping("/collections/{collectionName}/documents")
    public ResponseEntity<String> createDocument(
            @PathVariable("collectionName") String collectionName,
            @RequestBody String documentString
    ) {
        try {
            database.addDocument(collectionName, documentString);
            return ResponseEntity.ok().body("Added document successfully");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("An error occurred while added the document");
        } catch (DocumentSchemaViolationException | CollectionNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/collections/{collectionName}/indexes")
    public ResponseEntity<Collection<Map<String, Object>>> getCollectionIndexes(
            @PathVariable("collectionName") String collectionName
    ) {
        try {
            return ResponseEntity.ok(database.getCollectionIndexes(collectionName));
        } catch (CollectionNotFoundException e) {
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/collections/{collectionName}/indexes")
    public ResponseEntity<String> createCollectionIndex(
            @PathVariable String collectionName,
            @RequestBody String indexFields
    ) {
        try {
            database.createIndex(collectionName, indexFields);
            return ResponseEntity.ok("Creating the index successfully: " + indexFields);
        } catch (CollectionNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("An error occurred while accessing the indexes");
        }
    }

    @DeleteMapping("/collections/{collectionName}/indexes")
    public ResponseEntity<String> deleteCollectionIndex(
            @PathVariable String collectionName,
            @RequestBody String indexFields
    ) {
        try {
            database.deleteIndex(collectionName, indexFields);
            return ResponseEntity.ok("Deleted the index successfully: " + indexFields);
        } catch (CollectionNotFoundException | NoSuchIndexException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/collections/{collectionName}/schema")
    public ResponseEntity<Map<String, Object>> getSchema(
            @PathVariable("collectionName") String collectionName
    ) {
        try {
            return ResponseEntity.ok(database.getCollectionSchema(collectionName));
        } catch (CollectionNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

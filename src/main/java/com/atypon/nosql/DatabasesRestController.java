package com.atypon.nosql;

import com.atypon.nosql.database.*;
import com.atypon.nosql.database.document.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

@RestController
public class DatabasesRestController<T extends Document<?>> {
    private final DatabaseGenerator databaseGenerator;

    private final Map<String, Database> databases;

    public DatabasesRestController(DatabaseGenerator databaseGenerator, Map<String, Database> databases) {
        this.databaseGenerator = databaseGenerator;
        this.databases = databases;
    }

    @GetMapping("/databases")
    public ResponseEntity<Collection<String>> getDatabases() {
        return ResponseEntity.ok(databases.keySet());
    }

    @PostMapping("/databases/{database}")
    public ResponseEntity<String> createDatabase(@PathVariable("database") String database) {
        databases.put(database, databaseGenerator.create(Path.of(database + "/")));
        return ResponseEntity.ok("Database created: " + database);
    }

    @DeleteMapping("/databases/{database}")
    public ResponseEntity<String> deleteDatabase(@PathVariable("database") String database) {
        checkDatabaseExists(database);
        databases.remove(database);
        return ResponseEntity.ok("Database removed: " + database);
    }

    private void checkDatabaseExists(String database) {
        if (!databases.containsKey(database)) {
            throw new NoSuchDatabaseException(database);
        }
    }
}

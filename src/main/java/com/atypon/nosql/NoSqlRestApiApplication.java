package com.atypon.nosql;

import com.atypon.nosql.database.cache.LRUCache;
import com.atypon.nosql.database.collection.IndexedDocumentsCollection;
import com.atypon.nosql.database.collection.IndexedDocumentsCollectionFactory;
import com.atypon.nosql.database.document.Document;
import com.atypon.nosql.database.document.DocumentFactory;
import com.atypon.nosql.database.io.BasicIOEngine;
import com.atypon.nosql.database.io.CachedIOEngine;
import com.atypon.nosql.database.io.IOEngine;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class NoSqlRestApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(NoSqlRestApiApplication.class, args);
    }

    @Bean
    public Path databasesDirectory() {
        return Path.of("./db/databases");
    }

    @Bean
    public Path usersDirectory() {
        return Path.of("./db/users");
    }

    @Bean
    public IOEngine ioEngine(DocumentFactory documentFactory) {
        LRUCache<Path, Document> cache = new LRUCache<>(100000);
        return CachedIOEngine.from(new BasicIOEngine(documentFactory), cache);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public IndexedDocumentsCollection usersCollection(
            DocumentFactory documentFactory,
            Path usersDirectory,
            IndexedDocumentsCollectionFactory documentsCollectionFactory,
            PasswordEncoder passwordEncoder) {
        IndexedDocumentsCollection usersCollection;
        usersCollection = createUsersCollection(documentFactory, usersDirectory, documentsCollectionFactory);
        createUsernameIndex(usersCollection, documentFactory);
        createAdminUser(documentFactory, usersCollection, passwordEncoder);
        return usersCollection;
    }

    private IndexedDocumentsCollection createUsersCollection(DocumentFactory documentFactory, Path usersDirectory, IndexedDocumentsCollectionFactory documentsCollectionFactory) {
        IndexedDocumentsCollection usersCollection;
        if (Files.exists(usersDirectory)) {
            usersCollection = documentsCollectionFactory.createCollection(usersDirectory);
        } else {
            Map<String, Object> schemaMap = Map.of(
                    "username!", "string",
                    "password!", "string",
                    "roles!", List.of("string")
            );
            Document usersSchema = documentFactory.createFromMap(schemaMap);
            usersCollection = documentsCollectionFactory.createCollection(usersDirectory, usersSchema);
        }
        return usersCollection;
    }

    @Bean
    public List<String> secondaryNodesUrls(ApplicationArguments arguments) {
        return arguments.getOptionValues("node");
    }

    private void createUsernameIndex(IndexedDocumentsCollection usersCollection, DocumentFactory documentFactory) {
        Document usernameIndex = documentFactory.createFromString("{username: null}");
        if (!usersCollection.containsIndex(usernameIndex)) {
            usersCollection.createIndex(usernameIndex);
        }
    }

    private void createAdminUser(
            DocumentFactory documentFactory,
            IndexedDocumentsCollection usersCollection,
            PasswordEncoder passwordEncoder) {
        Document adminCriteria = documentFactory.createFromString("{username: \"admin\"}");
        if (!usersCollection.contains(adminCriteria)) {
            Map<String, Object> adminUserData = Map.of(
                    "username", "admin",
                    "password", passwordEncoder.encode("admin"),
                    "roles", List.of("ADMIN")
            );
            Document admin = documentFactory.createFromMap(adminUserData);
            usersCollection.addDocument(admin);
        }
    }
}

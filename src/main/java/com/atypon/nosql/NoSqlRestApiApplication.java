package com.atypon.nosql;

import com.atypon.nosql.database.DatabaseFactory;
import com.atypon.nosql.database.GenericDatabaseFactory;
import com.atypon.nosql.database.cache.LRUCache;
import com.atypon.nosql.database.collection.BasicIndexedDocumentsCollection;
import com.atypon.nosql.database.collection.IndexedDocumentsCollection;
import com.atypon.nosql.database.document.Document;
import com.atypon.nosql.database.document.DocumentFactory;
import com.atypon.nosql.database.document.DocumentSchemaFactory;
import com.atypon.nosql.database.document.RandomObjectIdGenerator;
import com.atypon.nosql.database.gsondocument.GsonDocumentFactory;
import com.atypon.nosql.database.gsondocument.GsonDocumentSchemaFactory;
import com.atypon.nosql.database.index.DefaultIndexFactory;
import com.atypon.nosql.database.index.IndexFactory;
import com.atypon.nosql.database.io.CachedIOEngine;
import com.atypon.nosql.database.io.DefaultIOEngine;
import com.atypon.nosql.database.io.IOEngine;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    public DocumentFactory documentFactory() {
        RandomObjectIdGenerator idGenerator = new RandomObjectIdGenerator();
        return new GsonDocumentFactory(idGenerator);
    }

    @Bean
    public IndexFactory indexFactory() {
        return new DefaultIndexFactory();
    }

    @Bean
    public IOEngine ioEngine(DocumentFactory documentFactory) {
        LRUCache<Path, Document> cache = new LRUCache<>(100000);
        return CachedIOEngine.from(new DefaultIOEngine(documentFactory), cache);
    }

    @Bean
    public DocumentSchemaFactory documentSchemaFactory() {
        return new GsonDocumentSchemaFactory();
    }

    @Bean
    public DatabaseFactory databaseGenerator(
            DocumentFactory documentFactory,
            DocumentSchemaFactory schemaFactory,
            IOEngine ioEngine,
            IndexFactory indexFactory
    ) {
        return GenericDatabaseFactory.builder()
                .setDocumentFactory(documentFactory)
                .setSchemaFactory(schemaFactory)
                .setIoEngine(ioEngine)
                .setIndexFactory(indexFactory)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public BasicIndexedDocumentsCollection usersCollection(
            DocumentFactory documentFactory,
            Path usersDirectory,
            IndexFactory indexFactory,
            IOEngine ioEngine
    ) {
        BasicIndexedDocumentsCollection usersCollection = BasicIndexedDocumentsCollection.builder()
                .setDocumentFactory(documentFactory)
                .setDocumentsPath(usersDirectory)
                .setIndexFactory(indexFactory)
                .setIOEngine(ioEngine)
                .build();
        createUsernameIndex(usersCollection, documentFactory);
        createAdminUser(documentFactory, usersCollection);
        return usersCollection;
    }

    private void createUsernameIndex(IndexedDocumentsCollection usersCollection, DocumentFactory documentFactory) {
        Document usernameIndex = documentFactory.createFromString("{username: null}");
        if (!usersCollection.containsIndex(usernameIndex)) {
            usersCollection.createIndex(usernameIndex);
        }
    }

    private void createAdminUser(DocumentFactory documentFactory, BasicIndexedDocumentsCollection usersCollection) {
        Document adminCriteria = documentFactory.createFromString("{username: \"admin\"}");
        if (!usersCollection.contains(adminCriteria)) {
            Map<String, Object> adminUserData = Map.of(
                    "username", "admin",
                    "password", passwordEncoder().encode("admin"),
                    "roles", List.of("ADMIN")
            );
            Document admin = documentFactory.createFromMap(adminUserData);
            admin = documentFactory.appendId(admin);
            usersCollection.addDocument(admin);
        }
    }
}

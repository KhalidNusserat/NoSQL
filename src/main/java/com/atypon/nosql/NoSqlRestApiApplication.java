package com.atypon.nosql;

import com.atypon.nosql.database.Database;
import com.atypon.nosql.database.DatabaseGenerator;
import com.atypon.nosql.database.GenericDatabaseGenerator;
import com.atypon.nosql.database.cache.LRUCache;
import com.atypon.nosql.database.document.DocumentGenerator;
import com.atypon.nosql.database.document.DocumentSchemaGenerator;
import com.atypon.nosql.database.document.RandomObjectIdGenerator;
import com.atypon.nosql.database.gsondocument.GsonDocument;
import com.atypon.nosql.database.gsondocument.GsonDocumentGenerator;
import com.atypon.nosql.database.gsondocument.GsonDocumentSchemaGenerator;
import com.atypon.nosql.database.io.CachedIOEngine;
import com.atypon.nosql.database.io.DefaultIOEngine;
import com.atypon.nosql.database.io.IOEngine;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SpringBootApplication
public class NoSqlRestApiApplication {
    private final Path databasesDirectory = Path.of("./databases");

    public static void main(String[] args) {
        SpringApplication.run(NoSqlRestApiApplication.class, args);
    }

    @Bean
    public IOEngine<GsonDocument> ioEngine() {
        return CachedIOEngine.from(new DefaultIOEngine<>(), new LRUCache<>(100000));
    }

    @Bean
    public DocumentGenerator<GsonDocument> documentGenerator() {
        return new GsonDocumentGenerator(new RandomObjectIdGenerator());
    }

    @Bean
    public DocumentSchemaGenerator<GsonDocument> documentSchemaGenerator() {
        return new GsonDocumentSchemaGenerator();
    }

    @Bean
    public DatabaseGenerator databaseGenerator() {
        return GenericDatabaseGenerator.<GsonDocument>builder()
                .setDatabasesDirectory(databasesDirectory)
                .setDocumentGenerator(documentGenerator())
                .setSchemaGenerator(documentSchemaGenerator())
                .setIoEngine(ioEngine())
                .build();
    }

    @Bean
    public Map<String, Database> databases() {
        return new ConcurrentHashMap<>();
    }
}

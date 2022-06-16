package com.atypon.nosql;

import com.atypon.nosql.database.DatabaseFactory;
import com.atypon.nosql.database.DatabasesManager;
import com.atypon.nosql.database.DefaultDatabasesManager;
import com.atypon.nosql.database.GenericDatabaseFactory;
import com.atypon.nosql.database.cache.LRUCache;
import com.atypon.nosql.database.document.DocumentFactory;
import com.atypon.nosql.database.document.DocumentSchemaFactory;
import com.atypon.nosql.database.document.RandomObjectIdGenerator;
import com.atypon.nosql.database.gsondocument.GsonDocumentFactory;
import com.atypon.nosql.database.gsondocument.GsonDocumentSchemaFactory;
import com.atypon.nosql.database.io.CachedIOEngine;
import com.atypon.nosql.database.io.DefaultIOEngine;
import com.atypon.nosql.database.io.IOEngine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.nio.file.Path;

@SpringBootApplication
public class NoSqlRestApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(NoSqlRestApiApplication.class, args);
    }

    @Bean
    public Path databasesDirectory() {
        return Path.of("./databases");
    }

    @Bean
    public DocumentFactory documentFactory() {
        return new GsonDocumentFactory(new RandomObjectIdGenerator());
    }

    @Bean
    public IOEngine ioEngine() {
        return CachedIOEngine.from(new DefaultIOEngine(documentFactory()), new LRUCache<>(100000));
    }

    @Bean
    public DocumentSchemaFactory documentSchemaGenerator() {
        return new GsonDocumentSchemaFactory();
    }

    @Bean
    public DatabaseFactory databaseGenerator() {
        return GenericDatabaseFactory.builder()
                .setDocumentGenerator(documentFactory())
                .setSchemaGenerator(documentSchemaGenerator())
                .setIoEngine(ioEngine())
                .build();
    }

    @Bean
    public DatabasesManager databasesManager() {
        return new DefaultDatabasesManager(databasesDirectory(), databaseGenerator());
    }
}

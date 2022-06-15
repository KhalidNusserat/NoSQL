package com.atypon.nosql;

import com.atypon.nosql.database.Database;
import com.atypon.nosql.database.GenericDatabase;
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

@SpringBootApplication
public class NoSqlRestApiApplication {
    private final Path collectionsDirectory = Path.of("./db/");

    public static void main(String[] args) {
        SpringApplication.run(NoSqlRestApiApplication.class, args);
    }

    @Bean
    IOEngine<GsonDocument> ioEngine() {
        return CachedIOEngine.from(new DefaultIOEngine<>(), new LRUCache<>(100000));
    }

    @Bean
    DocumentGenerator<GsonDocument> documentGenerator() {
        return new GsonDocumentGenerator(new RandomObjectIdGenerator());
    }

    @Bean
    DocumentSchemaGenerator<GsonDocument> schemaGenerator() {
        return new GsonDocumentSchemaGenerator();
    }

    @Bean
    Database database() {
        return new GenericDatabase<>(
                ioEngine(),
                collectionsDirectory,
                documentGenerator(),
                schemaGenerator()
        );
    }
}

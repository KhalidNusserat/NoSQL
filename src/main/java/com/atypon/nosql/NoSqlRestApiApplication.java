package com.atypon.nosql;

import com.atypon.nosql.database.DatabaseFactory;
import com.atypon.nosql.database.GenericDatabaseFactory;
import com.atypon.nosql.database.cache.LRUCache;
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

@SpringBootApplication
public class NoSqlRestApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(NoSqlRestApiApplication.class, args);
    }

    @Bean
    public DocumentFactory documentFactory() {
        return new GsonDocumentFactory(new RandomObjectIdGenerator());
    }

    @Bean
    public IndexFactory indexFactory() {
        return new DefaultIndexFactory();
    }

    @Bean
    public IOEngine ioEngine() {
        return CachedIOEngine.from(new DefaultIOEngine(documentFactory()), new LRUCache<>(100000));
    }

    @Bean
    public DocumentSchemaFactory documentSchemaFactory() {
        return new GsonDocumentSchemaFactory();
    }

    @Bean
    public DatabaseFactory databaseGenerator() {
        return GenericDatabaseFactory.builder()
                .setDocumentFactory(documentFactory())
                .setSchemaFactory(documentSchemaFactory())
                .setIoEngine(ioEngine())
                .setIndexFactory(indexFactory())
                .build();
    }

    @Bean
    public Path databasesDirectory() {
        return Path.of("./databases");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

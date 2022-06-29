package com.atypon.nosql;

import com.atypon.nosql.cache.Cache;
import com.atypon.nosql.cache.LRUCache;
import com.atypon.nosql.document.Document;
import com.atypon.nosql.document.DocumentFactory;
import com.atypon.nosql.index.Index;
import com.atypon.nosql.response.DatabaseResponse;
import com.atypon.nosql.storage.*;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.file.Path;
import java.util.List;

@SpringBootApplication
public class DatabaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(DatabaseApplication.class, args);
    }

    @Bean
    public Path databasesDirectory() {
        return Path.of("./db/databases");
    }

    @Bean
    public StorageEngine ioEngine() {
        LRUCache<Path, Document> documentCache = new LRUCache<>(100000);
        return StorageEngines.cached(
                StorageEngines.secured(
                        StorageEngines.basic()
                ),
                documentCache
        );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public List<String> remoteNodes(ApplicationArguments arguments) {
        return arguments.getOptionValues("node");
    }

    @Bean
    public Cache<String, DatabaseResponse> storedResultsCache() {
        return new LRUCache<>(100000);
    }
}

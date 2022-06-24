package com.atypon.nosql;

import com.atypon.nosql.cache.LRUCache;
import com.atypon.nosql.document.Document;
import com.atypon.nosql.document.DocumentFactory;
import com.atypon.nosql.storage.BasicStorageEngine;
import com.atypon.nosql.storage.CachedStorageEngine;
import com.atypon.nosql.storage.StorageEngine;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

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
    public StorageEngine ioEngine(DocumentFactory documentFactory) {
        LRUCache<Path, Document> cache = new LRUCache<>(100000);
        return CachedStorageEngine.from(new BasicStorageEngine(documentFactory), cache);
    }

//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }

    @Bean
    public List<String> secondaryNodesUrls(ApplicationArguments arguments) {
        return arguments.getOptionValues("node");
    }
}

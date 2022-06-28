package com.atypon.nosql;

import com.atypon.nosql.cache.LRUCache;
import com.atypon.nosql.document.Document;
import com.atypon.nosql.document.DocumentFactory;
import com.atypon.nosql.index.Index;
import com.atypon.nosql.request.DatabaseOperation;
import com.atypon.nosql.storage.BasicStorageEngine;
import com.atypon.nosql.storage.CachedStorageEngine;
import com.atypon.nosql.storage.StorageEngine;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

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
        LRUCache<Path, Document> documentCache = new LRUCache<>(100000);
        LRUCache<Path, Index> indexCache = new LRUCache<>(1000);
        return CachedStorageEngine.builder()
                .documentCache(documentCache)
                .indexCache(indexCache)
                .storageEngine(new BasicStorageEngine(documentFactory))
                .build();
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
    public Set<DatabaseOperation> synchronizedOperations() {
        return Set.of(
                DatabaseOperation.ADD_DOCUMENT,
                DatabaseOperation.REMOVE_DOCUMENTS,
                DatabaseOperation.UPDATE_DOCUMENTS,
                DatabaseOperation.CREATE_COLLECTION,
                DatabaseOperation.REMOVE_COLLECTION,
                DatabaseOperation.CREATE_INDEX,
                DatabaseOperation.REMOVE_INDEX,
                DatabaseOperation.CREATE_DATABASE,
                DatabaseOperation.REMOVE_DATABASE
        );
    }
}

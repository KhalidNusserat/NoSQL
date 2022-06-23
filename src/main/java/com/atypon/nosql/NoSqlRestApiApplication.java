package com.atypon.nosql;

import com.atypon.nosql.cache.LRUCache;
import com.atypon.nosql.document.Document;
import com.atypon.nosql.document.DocumentFactory;
import com.atypon.nosql.io.BasicIOEngine;
import com.atypon.nosql.io.CachedIOEngine;
import com.atypon.nosql.io.IOEngine;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.nio.file.Path;
import java.util.List;

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

//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }

    @Bean
    public List<String> secondaryNodesUrls(ApplicationArguments arguments) {
        return arguments.getOptionValues("node");
    }
}

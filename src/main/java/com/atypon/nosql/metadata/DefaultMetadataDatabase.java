package com.atypon.nosql.metadata;

import com.atypon.nosql.DatabasesManager;
import com.atypon.nosql.document.Document;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DefaultMetadataDatabase implements MetadataDatabase {

    private final DatabasesManager databasesManager;

    private static final String metadataDatabaseName = "metadata";

    public DefaultMetadataDatabase(DatabasesManager databasesManager) {
        this.databasesManager = databasesManager;
        databasesManager.createDatabase(metadataDatabaseName);
    }

    private void createUsersCollection() {
        if (!databasesManager.getCollectionsNames(metadataDatabaseName).contains("users")) {
            Map<String, Object> usersSchema = Map.of(
                    "username", "string",
                    "password", "string",
                    "authorities", List.of("string")
            );
            Map<String, Object> usernameIndex = new HashMap<>();
            usernameIndex.put("username", null);
            databasesManager.createCollection(metadataDatabaseName, "users", usersSchema);
            databasesManager.createIndex(metadataDatabaseName, "users", usernameIndex, true);
        }
    }
}

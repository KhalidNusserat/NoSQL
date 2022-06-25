package com.atypon.nosql.metadata;

import com.atypon.nosql.DatabasesManager;
import com.atypon.nosql.document.Document;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DefaultMetadataDatabase implements MetadataDatabase {

    private final DatabasesManager databasesManager;

    public DefaultMetadataDatabase(DatabasesManager databasesManager) {
        this.databasesManager = databasesManager;
        databasesManager.createDatabase("metadata");
        createUsersCollection();
    }

    private void createUsersCollection() {
        if (!databasesManager.getCollectionsNames("metadata").contains("users")) {
            Map<String, Object> usersSchema = Map.of(
                    "username", "string",
                    "password", "string"
            );
            Map<String, Object> usernameIndex = new HashMap<>();
            usernameIndex.put("username", null);
            databasesManager.createCollection("metadata", "users", usersSchema);
            databasesManager.createIndex("metadata", "users", usernameIndex, true);
        }
    }
}

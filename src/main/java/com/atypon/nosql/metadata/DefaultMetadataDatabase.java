package com.atypon.nosql.metadata;

import com.atypon.nosql.database.Database;
import com.atypon.nosql.database.DatabaseFactory;
import com.atypon.nosql.document.Document;
import com.atypon.nosql.document.DocumentFactory;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class DefaultMetadataDatabase implements MetadataDatabase {

    private final Database metadataDatabase;

    public DefaultMetadataDatabase(
            DatabaseFactory databaseFactory,
            Path databasesDirectory,
            DocumentFactory documentFactory) {
        Path metadataDatabaseDirectory = databasesDirectory.resolve("metadata/");
        metadataDatabase = databaseFactory.create(metadataDatabaseDirectory);
        createUsersCollection(documentFactory);
    }

    private void createUsersCollection(DocumentFactory documentFactory) {
        String userSchemaString = "{username: \"string\", password: \"string\"}";
        Document usersSchema = documentFactory.createFromString(userSchemaString);
        metadataDatabase.createCollection("users", usersSchema);
    }
}

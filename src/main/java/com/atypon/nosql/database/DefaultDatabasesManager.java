package com.atypon.nosql.database;

import com.atypon.nosql.NoSuchDatabaseException;
import com.atypon.nosql.database.document.Document;
import com.atypon.nosql.database.utils.FileUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultDatabasesManager<T extends Document> implements DatabasesManager<T> {
    private final Map<String, Database<T>> databases = new ConcurrentHashMap<>();

    private final Path databasesDirectory;

    private final DatabaseGenerator<T> databaseGenerator;

    public DefaultDatabasesManager(Path databasesDirectory, DatabaseGenerator<T> databaseGenerator) {
        this.databasesDirectory = databasesDirectory;
        this.databaseGenerator = databaseGenerator;
        FileUtils.createDirectories(databasesDirectory);
        loadDatabases();
    }

    public void loadDatabases() {
        FileUtils.traverseDirectory(databasesDirectory)
                .filter(path -> Files.isDirectory(path) && !path.equals(databasesDirectory))
                .forEach(this::loadDatabase);
    }

    private void loadDatabase(Path databaseDirectory) {
        String databaseName = databaseDirectory.getFileName().toString();
        databases.put(databaseName, databaseGenerator.create(databaseDirectory));
    }

    @Override
    public void create(String databaseName) {
        Path databaseDirectory = databasesDirectory.resolve(databaseName + "/");
        databases.put(databaseName, databaseGenerator.create(databaseDirectory));
    }

    private void checkDatabaseExists(String database) {
        if (!databases.containsKey(database)) {
            throw new NoSuchDatabaseException(database);
        }
    }

    @Override
    public Database<T> get(String databaseName) {
        checkDatabaseExists(databaseName);
        return databases.get(databaseName);
    }

    @Override
    public void remove(String databaseName) {
        checkDatabaseExists(databaseName);
        databases.get(databaseName).deleteDatabase();
        databases.remove(databaseName);
    }

    @Override
    public Collection<String> getDatabasesNames() {
        return databases.keySet();
    }
}

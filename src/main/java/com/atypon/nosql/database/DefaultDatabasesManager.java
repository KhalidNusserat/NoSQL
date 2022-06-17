package com.atypon.nosql.database;

import com.atypon.nosql.api.NoSuchDatabaseException;
import com.atypon.nosql.database.utils.FileUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultDatabasesManager implements DatabasesManager {
    private final Map<String, Database> databases = new ConcurrentHashMap<>();

    private final Path databasesDirectory;

    private final DatabaseFactory databaseFactory;

    public DefaultDatabasesManager(Path databasesDirectory, DatabaseFactory databaseFactory) {
        this.databasesDirectory = databasesDirectory;
        this.databaseFactory = databaseFactory;
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
        databases.put(databaseName, databaseFactory.create(databaseDirectory));
    }

    @Override
    public void create(String databaseName) {
        Path databaseDirectory = databasesDirectory.resolve(databaseName + "/");
        databases.put(databaseName, databaseFactory.create(databaseDirectory));
    }

    private void checkDatabaseExists(String database) {
        if (!databases.containsKey(database)) {
            throw new NoSuchDatabaseException(database);
        }
    }

    @Override
    public Database get(String databaseName) {
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

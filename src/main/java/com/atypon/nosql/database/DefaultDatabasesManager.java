package com.atypon.nosql.database;

import com.atypon.nosql.database.utils.ExtraFileUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultDatabasesManager implements DatabasesManager {
    private final Map<String, Database> databases = new ConcurrentHashMap<>();

    private final Path databasesDirectory;

    private final DatabaseGenerator databaseGenerator;

    public DefaultDatabasesManager(Path databasesDirectory, DatabaseGenerator databaseGenerator) {
        this.databasesDirectory = databasesDirectory;
        this.databaseGenerator = databaseGenerator;
        ExtraFileUtils.createDirectories(databasesDirectory);
        loadDatabases();
    }

    public void loadDatabases() {
        ExtraFileUtils.traverseDirectory(databasesDirectory)
                .filter(path -> Files.isDirectory(path) && !path.equals(databasesDirectory))
                .forEach(this::loadDatabase);
    }

    private void loadDatabase(Path databaseDirectory) {
        String databaseName = databaseDirectory.getFileName().toString();
        databases.put(databaseName, databaseGenerator.create(databaseDirectory));
    }

    @Override
    public Database get(String databaseName) {
        return databases.get(databaseName);
    }

    @Override
    public void create(String databaseName) {
        Path databaseDirectory = databasesDirectory.resolve(databaseName + "/");
        databases.put(databaseName, databaseGenerator.create(databaseDirectory));
    }

    @Override
    public void remove(String databaseName) {
        databases.get(databaseName).deleteDatabase();
        databases.remove(databaseName);
    }

    @Override
    public boolean contains(String databaseName) {
        return databases.containsKey(databaseName);
    }

    @Override
    public Collection<String> getDatabasesNames() {
        return databases.keySet();
    }
}

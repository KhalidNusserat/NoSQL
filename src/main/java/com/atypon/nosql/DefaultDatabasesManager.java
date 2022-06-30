package com.atypon.nosql;

import com.atypon.nosql.database.Database;
import com.atypon.nosql.database.DatabaseFactory;
import com.atypon.nosql.utils.FileUtils;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ToString
@Component
public class DefaultDatabasesManager implements DatabasesManager {

    private final Path databasesDirectory;

    private final Map<String, Database> databases = new ConcurrentHashMap<>();

    private final DatabaseFactory databaseFactory;

    public DefaultDatabasesManager(Path databasesDirectory, DatabaseFactory databaseFactory) {
        this.databasesDirectory = databasesDirectory;
        this.databaseFactory = databaseFactory;
        FileUtils.createDirectories(databasesDirectory);
        loadDatabases();
    }

    private void loadDatabases() {
        FileUtils.traverseDirectory(databasesDirectory)
                .filter(path -> Files.isDirectory(path) && !path.equals(databasesDirectory))
                .forEach(this::loadDatabase);
    }

    private void loadDatabase(Path databaseDirectory) {
        String databaseName = databaseDirectory.getFileName().toString();
        databases.put(databaseName, databaseFactory.create(databaseDirectory));
    }

    @Override
    public void createDatabase(String databaseName) {
        Path databaseDirectory = databasesDirectory.resolve(databaseName + "/");
        if (!databases.containsKey(databaseName)) {
            databases.put(databaseName, databaseFactory.create(databaseDirectory));
        }
    }

    @Override
    public void removeDatabase(String databaseName) {
        checkDatabaseExists(databaseName);
        databases.get(databaseName).deleteDatabase();
        databases.remove(databaseName);
    }

    @Override
    public boolean containsDatabase(String databaseName) {
        return databases.containsKey(databaseName);
    }

    private void checkDatabaseExists(String database) {
        if (!databases.containsKey(database)) {
            throw new NoSuchDatabaseException(database);
        }
    }

    @Override
    public Collection<String> getDatabasesNames() {
        return databases.keySet();
    }

    @Override
    public Database getDatabase(String databaseName) {
        checkDatabaseExists(databaseName);
        return databases.get(databaseName);
    }
}

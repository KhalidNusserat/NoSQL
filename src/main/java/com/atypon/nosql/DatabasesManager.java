package com.atypon.nosql;

import com.atypon.nosql.database.Database;

import java.util.Collection;

public interface DatabasesManager {
    void createDatabase(String databaseName);

    void removeDatabase(String databaseName);

    boolean containsDatabase(String databaseName);

    Collection<String> getDatabasesNames();

    Database getDatabase(String databaseName);
}

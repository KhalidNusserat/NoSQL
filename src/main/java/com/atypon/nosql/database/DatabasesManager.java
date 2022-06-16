package com.atypon.nosql.database;

import java.util.Collection;

public interface DatabasesManager {
    void create(String databaseName);

    Database get(String databaseName);

    void remove(String databaseName);

    Collection<String> getDatabasesNames();
}

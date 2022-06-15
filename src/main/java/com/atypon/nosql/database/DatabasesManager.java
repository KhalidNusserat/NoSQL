package com.atypon.nosql.database;

import java.util.Collection;

public interface DatabasesManager {
    Database get(String databaseName);

    void create(String databaseName);

    void remove(String databaseName);

    boolean contains(String databaseName);

    Collection<String> getDatabasesNames();
}

package com.atypon.nosql.api.services;

import com.atypon.nosql.database.Database;

import java.util.Collection;

public interface DatabasesService {
    void create(String databaseName);

    Database get(String databaseName);

    void remove(String databaseName);

    Collection<String> getDatabasesNames();
}

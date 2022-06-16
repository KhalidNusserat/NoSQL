package com.atypon.nosql.database;

import com.atypon.nosql.database.document.Document;

import java.util.Collection;

public interface DatabasesManager<T extends Document> {
    void create(String databaseName);

    Database<T> get(String databaseName);

    void remove(String databaseName);

    Collection<String> getDatabasesNames();
}

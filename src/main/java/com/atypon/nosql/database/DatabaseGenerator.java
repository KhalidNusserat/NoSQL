package com.atypon.nosql.database;

import com.atypon.nosql.database.document.Document;

import java.nio.file.Path;

public interface DatabaseGenerator<T extends Document> {
    Database<T> create(Path databaseDirectory);
}

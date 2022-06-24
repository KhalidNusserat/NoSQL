package com.atypon.nosql.database;

import java.nio.file.Path;

public interface DatabaseFactory {
    Database create(Path databaseDirectory);
}

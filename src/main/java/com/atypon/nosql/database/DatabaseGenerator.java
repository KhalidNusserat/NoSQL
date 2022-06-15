package com.atypon.nosql.database;

import java.nio.file.Path;

public interface DatabaseGenerator {
    Database create(Path databaseDirectory);
}

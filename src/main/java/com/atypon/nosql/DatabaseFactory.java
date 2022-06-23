package com.atypon.nosql;

import java.nio.file.Path;

public interface DatabaseFactory {
    Database create(Path databaseDirectory);
}

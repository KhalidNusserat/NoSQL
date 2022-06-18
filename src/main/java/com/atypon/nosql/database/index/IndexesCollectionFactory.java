package com.atypon.nosql.database.index;

import java.nio.file.Path;

public interface IndexesCollectionFactory {
    IndexesCollection createIndexesCollection(Path indexesDirectory);
}

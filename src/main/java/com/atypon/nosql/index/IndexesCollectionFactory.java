package com.atypon.nosql.index;

import java.nio.file.Path;

public interface IndexesCollectionFactory {
    IndexesCollection createIndexesCollection(Path indexesDirectory, Path documentsDirectory);
}

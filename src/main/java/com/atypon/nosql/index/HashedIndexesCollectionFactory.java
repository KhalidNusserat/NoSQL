package com.atypon.nosql.index;

import com.atypon.nosql.document.DocumentFactory;
import com.atypon.nosql.storage.StorageEngine;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class HashedIndexesCollectionFactory implements IndexesCollectionFactory {

    private final StorageEngine storageEngine;

    public HashedIndexesCollectionFactory(
            StorageEngine storageEngine) {
        this.storageEngine = storageEngine;
    }

    @Override
    public IndexesCollection createIndexesCollection(Path indexesDirectory, Path documentsDirectory) {
        return new HashedIndexesCollection(indexesDirectory, documentsDirectory, storageEngine);
    }
}

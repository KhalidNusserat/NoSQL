package com.atypon.nosql.index;

import com.atypon.nosql.storage.StorageEngine;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@ToString
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

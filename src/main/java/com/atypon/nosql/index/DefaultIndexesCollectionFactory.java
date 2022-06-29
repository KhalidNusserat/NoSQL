package com.atypon.nosql.index;

import com.atypon.nosql.document.DocumentFactory;
import com.atypon.nosql.storage.StorageEngine;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class DefaultIndexesCollectionFactory implements IndexesCollectionFactory {

    private final StorageEngine storageEngine;

    private final DocumentFactory documentFactory;

    public DefaultIndexesCollectionFactory(
            StorageEngine storageEngine,
            DocumentFactory documentFactory) {
        this.storageEngine = storageEngine;
        this.documentFactory = documentFactory;
    }

    @Override
    public IndexesCollection createIndexesCollection(Path indexesDirectory, Path documentsDirectory) {
        return new DefaultIndexesCollection(indexesDirectory, documentsDirectory, storageEngine, documentFactory);
    }
}

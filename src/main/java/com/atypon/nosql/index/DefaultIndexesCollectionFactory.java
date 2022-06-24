package com.atypon.nosql.index;

import com.atypon.nosql.document.DocumentFactory;
import com.atypon.nosql.storage.StorageEngine;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class DefaultIndexesCollectionFactory implements IndexesCollectionFactory {

    private final StorageEngine storageEngine;

    private final DocumentFactory documentFactory;

    private final IndexDocumentConverter indexDocumentConverter;

    public DefaultIndexesCollectionFactory(
            StorageEngine storageEngine,
            DocumentFactory documentFactory,
            IndexDocumentConverter indexDocumentConverter) {
        this.storageEngine = storageEngine;
        this.documentFactory = documentFactory;
        this.indexDocumentConverter = indexDocumentConverter;
    }

    @Override
    public IndexesCollection createIndexesCollection(Path indexesDirectory) {
        return new DefaultIndexesCollection(indexesDirectory, storageEngine, documentFactory, indexDocumentConverter);
    }
}

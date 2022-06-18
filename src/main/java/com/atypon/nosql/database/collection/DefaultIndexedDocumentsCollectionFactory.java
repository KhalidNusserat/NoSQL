package com.atypon.nosql.database.collection;

import com.atypon.nosql.database.index.IndexesCollectionFactory;
import com.atypon.nosql.database.io.IOEngine;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class DefaultIndexedDocumentsCollectionFactory implements IndexedDocumentsCollectionFactory {
    private final IOEngine ioEngine;

    private final BasicDocumentsCollectionFactory documentsCollectionFactory;

    private final IndexesCollectionFactory indexesCollectionFactory;

    public DefaultIndexedDocumentsCollectionFactory(
            IOEngine ioEngine,
            BasicDocumentsCollectionFactory documentsCollectionFactory,
            IndexesCollectionFactory indexesCollectionFactory) {
        this.ioEngine = ioEngine;
        this.documentsCollectionFactory = documentsCollectionFactory;
        this.indexesCollectionFactory = indexesCollectionFactory;
    }

    @Override
    public IndexedDocumentsCollection createCollection(Path collectionPath) {
        return new DefaultIndexedDocumentsCollection(
                collectionPath,
                ioEngine,
                documentsCollectionFactory,
                indexesCollectionFactory
        );
    }
}

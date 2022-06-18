package com.atypon.nosql.database.collection;

import com.atypon.nosql.database.io.IOEngine;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class DefaultBasicDocumentsCollectionFactory implements BasicDocumentsCollectionFactory {
    private final IOEngine ioEngine;

    public DefaultBasicDocumentsCollectionFactory(IOEngine ioEngine) {
        this.ioEngine = ioEngine;
    }

    @Override
    public DocumentsCollection createCollection(Path collectionPath) {
        return new DefaultBasicDocumentsCollection(collectionPath, ioEngine);
    }
}

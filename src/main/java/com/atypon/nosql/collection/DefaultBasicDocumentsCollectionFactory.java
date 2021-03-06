package com.atypon.nosql.collection;

import com.atypon.nosql.storage.StorageEngine;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@ToString
@Component
public class DefaultBasicDocumentsCollectionFactory implements BasicDocumentsCollectionFactory {

    private final StorageEngine storageEngine;

    public DefaultBasicDocumentsCollectionFactory(StorageEngine storageEngine) {
        this.storageEngine = storageEngine;
    }

    @Override
    public DocumentsCollection createCollection(Path collectionPath) {
        return new DefaultBasicDocumentsCollection(collectionPath, storageEngine);
    }
}

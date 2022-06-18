package com.atypon.nosql.database.collection;

import java.nio.file.Path;

public interface IndexedDocumentsCollectionFactory {
    IndexedDocumentsCollection createCollection(Path collectionPath);
}

package com.atypon.nosql.database.collection;

import java.nio.file.Path;

public interface BasicDocumentsCollectionFactory {
    DocumentsCollection createCollection(Path collectionPath);
}

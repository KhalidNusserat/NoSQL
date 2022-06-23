package com.atypon.nosql.collection;

import java.nio.file.Path;

public interface BasicDocumentsCollectionFactory {
    DocumentsCollection createCollection(Path collectionPath);
}

package com.atypon.nosql.collection;

import com.atypon.nosql.document.Document;

import java.nio.file.Path;

public interface IndexedDocumentsCollectionFactory {
    IndexedDocumentsCollection createCollection(Path collectionPath, Document schemaDocument);

    IndexedDocumentsCollection createCollection(Path collectionPath);
}

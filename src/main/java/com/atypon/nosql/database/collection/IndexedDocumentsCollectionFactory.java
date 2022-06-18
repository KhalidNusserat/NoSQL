package com.atypon.nosql.database.collection;

import com.atypon.nosql.database.document.Document;

import java.nio.file.Path;

public interface IndexedDocumentsCollectionFactory {
    IndexedDocumentsCollection createCollection(Path collectionPath, Document schemaDocument);

    IndexedDocumentsCollection createCollection(Path collectionPath);
}

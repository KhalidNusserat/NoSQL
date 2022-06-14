package com.atypon.nosql.database.collection;

import com.atypon.nosql.database.document.Document;

import java.nio.file.Path;

public interface DocumentsCollectionFactory<T extends Document<?>> {
    DocumentsCollection<T> createDefaultDocumentsCollection(Path collectionPath);

    IndexedDocumentsCollection<T> createIndexedDocumentsCollection(Path collectionPath);
}

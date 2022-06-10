package com.atypon.nosql.collection;

import com.atypon.nosql.document.Document;

import java.nio.file.Path;

public interface DocumentsCollectionFactory<T extends Document<?>> {
    DocumentsCollection<T> createCollection(DocumentCollectionType documentCollectionType, Path collectionPath);
}

package com.atypon.nosql.collection;

class DefaultGsonDocumentsCollectionTest extends DocumentsCollectionTest<DefaultGsonDocumentsCollection> {
    @Override
    public DefaultGsonDocumentsCollection create() {
        return new DefaultGsonDocumentsCollection(documentsIO, testDirectory);
    }
}
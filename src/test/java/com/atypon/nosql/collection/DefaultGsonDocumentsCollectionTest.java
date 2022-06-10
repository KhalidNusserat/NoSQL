package com.atypon.nosql.collection;

class DefaultGsonDocumentsCollectionTest extends DocumentsCollectionTest<DefaultGsonDocumentsCollection> {
    @Override
    public DefaultGsonDocumentsCollection create() {
        return DefaultGsonDocumentsCollection.from(documentsIO, testDirectory);
    }
}
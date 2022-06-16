package com.atypon.nosql.database.collection;

class BasicDocumentsCollectionTest extends DocumentsCollectionTest<BasicDocumentsCollection> {
    @Override
    public BasicDocumentsCollection create() {
        return new BasicDocumentsCollection(testDirectory, ioEngine);
    }
}
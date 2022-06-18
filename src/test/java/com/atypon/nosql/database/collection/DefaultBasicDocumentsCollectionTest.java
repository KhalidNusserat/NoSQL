package com.atypon.nosql.database.collection;

class DefaultBasicDocumentsCollectionTest extends DocumentsCollectionTest<DefaultBasicDocumentsCollection> {
    @Override
    public DefaultBasicDocumentsCollection create() {
        return new DefaultBasicDocumentsCollection(testDirectory, ioEngine);
    }
}
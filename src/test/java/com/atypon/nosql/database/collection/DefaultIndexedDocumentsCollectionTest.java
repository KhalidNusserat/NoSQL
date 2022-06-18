package com.atypon.nosql.database.collection;

class DefaultIndexedDocumentsCollectionTest extends DocumentsCollectionTest<DefaultIndexedDocumentsCollection> {
    private final IndexedDocumentsCollectionFactory collectionFactory;

    DefaultIndexedDocumentsCollectionTest(IndexedDocumentsCollectionFactory collectionFactory) {
        this.collectionFactory = collectionFactory;
    }

    @Override
    public DefaultIndexedDocumentsCollection create() {
        return (DefaultIndexedDocumentsCollection) collectionFactory.createCollection(testDirectory);
    }
}
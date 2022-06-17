package com.atypon.nosql.database.collection;

import com.atypon.nosql.database.index.DefaultIndexFactory;

class BasicIndexedDocumentsCollectionTest extends DocumentsCollectionTest<BasicIndexedDocumentsCollection> {
    private final DefaultIndexFactory indexGenerator = new DefaultIndexFactory();

    @Override
    public BasicIndexedDocumentsCollection create() {
        return BasicIndexedDocumentsCollection.builder()
                .setDocumentFactory(documentFactory)
                .setDocumentsPath(testDirectory)
                .setIndexFactory(indexGenerator)
                .setIOEngine(ioEngine)
                .build();
    }
}
package com.atypon.nosql.database.collection;

import com.atypon.nosql.database.gsondocument.GsonDocument;
import com.atypon.nosql.database.index.DefaultIndexFactory;

class BasicIndexedDocumentsCollectionTest extends DocumentsCollectionTest<BasicIndexedDocumentsCollection> {
    private final DefaultIndexFactory indexGenerator = new DefaultIndexFactory();

    @Override
    public BasicIndexedDocumentsCollection create() {
        return BasicIndexedDocumentsCollection.builder()
                .setDocumentGenerator(documentFactory)
                .setDocumentsPath(testDirectory)
                .setIndexGenerator(indexGenerator)
                .setIOEngine(ioEngine)
                .build();
    }
}
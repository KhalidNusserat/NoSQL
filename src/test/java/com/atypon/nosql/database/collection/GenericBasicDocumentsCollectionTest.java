package com.atypon.nosql.database.collection;

import com.atypon.nosql.database.gsondocument.GsonDocument;

class GenericBasicDocumentsCollectionTest extends
        DocumentsCollectionTest<GenericBasicDocumentsCollection<GsonDocument>> {
    @Override
    public GenericBasicDocumentsCollection<GsonDocument> create() {
        return GenericBasicDocumentsCollection.<GsonDocument>builder()
                .setDocumentsGenerator(documentGenerator)
                .setIoEngine(ioEngine)
                .setDocumentsPath(testDirectory)
                .build();
    }
}
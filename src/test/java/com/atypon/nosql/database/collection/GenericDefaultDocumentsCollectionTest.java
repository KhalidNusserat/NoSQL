package com.atypon.nosql.database.collection;

import com.atypon.nosql.database.gsondocument.GsonDocument;

class GenericDefaultDocumentsCollectionTest extends
        DocumentsCollectionTest<GenericDefaultDocumentsCollection<GsonDocument>> {
    @Override
    public GenericDefaultDocumentsCollection<GsonDocument> create() {
        return new GenericDefaultDocumentsCollection<>(ioEngine, testDirectory, documentGenerator);
    }
}
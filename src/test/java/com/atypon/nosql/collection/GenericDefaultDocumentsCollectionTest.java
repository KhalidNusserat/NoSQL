package com.atypon.nosql.collection;

import com.atypon.nosql.gsondocument.GsonDocument;

class GenericDefaultDocumentsCollectionTest extends
        DocumentsCollectionTest<GenericDefaultDocumentsCollection<GsonDocument>> {
    @Override
    public GenericDefaultDocumentsCollection<GsonDocument> create() {
        return new GenericDefaultDocumentsCollection<>(ioEngine, testDirectory, documentGenerator);
    }
}
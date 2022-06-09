package com.atypon.nosql.collection;

import com.atypon.nosql.gsondocument.GsonDocument;

class DefaultDocumentsCollectionTest extends DocumentsCollectionTest<DefaultDocumentsCollection<GsonDocument>> {
    @Override
    public DefaultDocumentsCollection<GsonDocument> create() {
        return DefaultDocumentsCollection.from(documentsIO, testDirectory);
    }
}
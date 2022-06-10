package com.atypon.nosql.collection;

import com.atypon.nosql.index.GsonDocumentFieldIndexManager;

class IndexedGsonDocumentsCollectionTest extends DocumentsCollectionTest<IndexedGsonDocumentsCollection> {

    private final GsonDocumentFieldIndexManager fieldIndexManager = new GsonDocumentFieldIndexManager(documentsIO, gson);

    @Override
    public IndexedGsonDocumentsCollection create() {
        return new IndexedGsonDocumentsCollection(documentsIO, testDirectory, fieldIndexManager);
    }
}
package com.atypon.nosql.collection;

import com.atypon.nosql.gsondocument.GsonDocument;
import com.atypon.nosql.index.GsonDocumentFieldIndexManager;
import com.google.gson.JsonElement;

class IndexedDocumentsCollectionTest extends
        DocumentsCollectionTest<IndexedDocumentsCollection<JsonElement, GsonDocument>> {
    @Override
    public IndexedDocumentsCollection<JsonElement, GsonDocument> create() {
        return new IndexedDocumentsCollection<>(
                documentsIO, testDirectory, new GsonDocumentFieldIndexManager(documentsIO, gson));
    }
}
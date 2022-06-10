package com.atypon.nosql.collection;

import com.atypon.nosql.gsondocument.GsonDocument;
import com.google.gson.JsonElement;

class DefaultDocumentsCollectionTest extends
        DocumentsCollectionTest<DefaultDocumentsCollection<JsonElement, GsonDocument>> {
    @Override
    public DefaultDocumentsCollection<JsonElement, GsonDocument> create() {
        return DefaultDocumentsCollection.from(documentsIO, testDirectory);
    }
}
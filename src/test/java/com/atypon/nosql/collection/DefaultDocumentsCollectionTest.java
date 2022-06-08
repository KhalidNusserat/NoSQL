package com.atypon.nosql.collection;

import com.atypon.nosql.gsondocument.GsonDocument;
import com.atypon.nosql.io.GsonCopyOnWriteIO;
import com.google.gson.JsonElement;

class DefaultDocumentsCollectionTest extends DocumentsCollectionTest<DefaultDocumentsCollection<JsonElement, GsonDocument>> {

    @Override
    public DefaultDocumentsCollection<JsonElement, GsonDocument> create() {
        return DefaultDocumentsCollection.<JsonElement, GsonDocument>builder()
                .setDirectoryPath(testDirectory)
                .setDocumentParser(parser)
                .setIO(new GsonCopyOnWriteIO())
                .create();
    }
}
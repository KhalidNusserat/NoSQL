package com.atypon.nosql.collection;

import com.atypon.nosql.gsondocument.GsonDocument;
import com.atypon.nosql.index.GenericIndexGenerator;

class GenericIndexedDocumentsCollectionTest extends
        DocumentsCollectionTest<GenericIndexedDocumentsCollection<GsonDocument>> {
    private final GenericIndexGenerator<GsonDocument> indexGenerator = new GenericIndexGenerator<>();

    @Override
    public GenericIndexedDocumentsCollection<GsonDocument> create() {
        return new GenericIndexedDocumentsCollection<>(
                testDirectory,
                documentGenerator,
                indexGenerator,
                ioEngine
        );
    }
}
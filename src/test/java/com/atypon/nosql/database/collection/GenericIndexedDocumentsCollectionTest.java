package com.atypon.nosql.database.collection;

import com.atypon.nosql.database.gsondocument.GsonDocument;
import com.atypon.nosql.database.index.GenericIndexGenerator;

class GenericIndexedDocumentsCollectionTest extends
        DocumentsCollectionTest<GenericIndexedDocumentsCollection<GsonDocument>> {
    private final GenericIndexGenerator<GsonDocument> indexGenerator = new GenericIndexGenerator<>();

    @Override
    public GenericIndexedDocumentsCollection<GsonDocument> create() {
        return GenericIndexedDocumentsCollection.<GsonDocument>builder()
                .setDocumentGenerator(documentGenerator)
                .setDocumentsPath(testDirectory)
                .setIndexGenerator(indexGenerator)
                .setIOEngine(ioEngine)
                .build();
    }
}
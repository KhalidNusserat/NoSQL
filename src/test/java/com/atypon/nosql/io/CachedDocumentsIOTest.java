package com.atypon.nosql.io;

import com.atypon.nosql.cache.LRUCache;
import com.atypon.nosql.collection.DocumentsCollectionTest;
import com.atypon.nosql.gsondocument.GsonDocument;
import com.atypon.nosql.gsondocument.GsonDocumentParser;

import static org.junit.jupiter.api.Assertions.*;

class CachedDocumentsIOTest extends DocumentsIOTest {

    @Override
    public DocumentsIO<GsonDocument> create() {
        return CachedDocumentsIO.from(
                new GsonDocumentsIO(new GsonDocumentParser()),
                new LRUCache<>(100)
        );
    }
}
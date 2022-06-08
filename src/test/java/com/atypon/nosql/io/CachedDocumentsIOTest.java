package com.atypon.nosql.io;

import com.atypon.nosql.cache.LRUCache;
import com.atypon.nosql.gsondocument.GsonDocument;
import com.atypon.nosql.gsondocument.GsonDocumentParser;

class CachedDocumentsIOTest extends DocumentsIOTest {

    @Override
    public DocumentsIO<GsonDocument> create() {
        return CachedDocumentsIO.from(
                new GsonDocumentsIO(new GsonDocumentParser()),
                new LRUCache<>(100)
        );
    }
}
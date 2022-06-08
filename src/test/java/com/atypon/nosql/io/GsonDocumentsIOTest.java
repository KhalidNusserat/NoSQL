package com.atypon.nosql.io;

import com.atypon.nosql.gsondocument.GsonDocument;
import com.atypon.nosql.gsondocument.GsonDocumentParser;

class GsonDocumentsIOTest extends DocumentsIOTest {

    @Override
    public DocumentsIO<GsonDocument> create() {
        return new GsonDocumentsIO(new GsonDocumentParser());
    }
}
package com.atypon.nosql.io;

import com.atypon.nosql.gsondocument.GsonDocument;
import com.atypon.nosql.gsondocument.GsonDocumentsIO;
import com.google.gson.Gson;

class GsonDocumentsIOTest extends DocumentsIOTest {

    @Override
    public DocumentsIO<GsonDocument> create() {
        return new GsonDocumentsIO(new Gson());
    }
}
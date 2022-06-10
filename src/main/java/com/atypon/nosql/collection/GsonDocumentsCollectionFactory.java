package com.atypon.nosql.collection;

import com.atypon.nosql.gsondocument.GsonDocument;
import com.atypon.nosql.index.GsonDocumentFieldIndexManager;
import com.atypon.nosql.io.DocumentsIO;
import com.google.gson.Gson;

import java.nio.file.Path;

public class GsonDocumentsCollectionFactory implements DocumentsCollectionFactory<GsonDocument> {
    private final DocumentsIO<GsonDocument> documentsIO;

    private final GsonDocumentFieldIndexManager fieldIndexManager;
    public GsonDocumentsCollectionFactory(DocumentsIO<GsonDocument> documentsIO, Gson gson) {
        this.documentsIO = documentsIO;
        this.fieldIndexManager = new GsonDocumentFieldIndexManager(documentsIO, gson);
    }

    @Override
    public DocumentsCollection<GsonDocument> createCollection(
            DocumentCollectionType documentCollectionType, Path collectionPath) {
        if (documentCollectionType == DocumentCollectionType.INDEXED) {
            return new IndexedGsonDocumentsCollection(documentsIO, collectionPath, fieldIndexManager);
        } else {
            return new DefaultGsonDocumentsCollection(documentsIO, collectionPath);
        }
    }
}

package com.atypon.nosql.collection;

import com.atypon.nosql.document.Document;
import com.atypon.nosql.document.DocumentGenerator;
import com.atypon.nosql.index.GenericIndexGenerator;
import com.atypon.nosql.io.IOEngine;

import java.nio.file.Path;

public class GenericDocumentsCollectionFactory<T extends Document<?>>
        implements DocumentsCollectionFactory<T> {
    private final IOEngine ioEngine;

    private final GenericIndexGenerator<T> indexGenerator;

    private final DocumentGenerator<T> documentGenerator;

    public GenericDocumentsCollectionFactory(IOEngine ioEngine, DocumentGenerator<T> documentGenerator) {
        this.ioEngine = ioEngine;
        this.documentGenerator = documentGenerator;
        indexGenerator = new GenericIndexGenerator<>();
    }

    @Override
    public DocumentsCollection<T> createDefaultDocumentsCollection(Path collectionPath) {
        return new GenericDefaultDocumentsCollection<>(ioEngine, collectionPath, documentGenerator);
    }

    @Override
    public IndexedDocumentsCollection<T> createIndexedDocumentsCollection(Path collectionPath) {
        return new GenericIndexedDocumentsCollection<>(collectionPath, documentGenerator, indexGenerator, ioEngine);
    }
}

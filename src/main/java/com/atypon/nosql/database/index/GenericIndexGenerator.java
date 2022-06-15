package com.atypon.nosql.database.index;

import com.atypon.nosql.database.document.Document;
import com.atypon.nosql.database.document.DocumentGenerator;
import com.atypon.nosql.database.io.IOEngine;

import java.nio.file.Path;

public class GenericIndexGenerator<T extends Document> implements IndexGenerator<T> {
    @Override
    public Index<T> createNewIndex(
            T fieldsDocument,
            Path indexPath,
            IOEngine<T> ioEngine,
            DocumentGenerator<T> documentGenerator
    ) {
        return new DefaultIndex<>(fieldsDocument, indexPath, ioEngine, documentGenerator);
    }
}
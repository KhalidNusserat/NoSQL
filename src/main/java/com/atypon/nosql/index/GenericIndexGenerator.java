package com.atypon.nosql.index;

import com.atypon.nosql.document.Document;
import com.atypon.nosql.document.DocumentGenerator;
import com.atypon.nosql.io.IOEngine;

import java.nio.file.Path;

public class GenericIndexGenerator<T extends Document<?>> implements IndexGenerator<T> {
    @Override
    public Index<T> createNewIndex(
            T fieldsDocument,
            Path indexPath,
            IOEngine ioEngine,
            DocumentGenerator<T> documentGenerator
    ) {
        return new DefaultIndex<>(fieldsDocument, indexPath, ioEngine, documentGenerator);
    }
}
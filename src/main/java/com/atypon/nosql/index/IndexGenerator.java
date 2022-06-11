package com.atypon.nosql.index;

import com.atypon.nosql.document.Document;
import com.atypon.nosql.document.DocumentGenerator;
import com.atypon.nosql.io.IOEngine;

import java.nio.file.Path;

public interface IndexGenerator<T extends Document<?>> {
    Index<T> createNewIndex(
            T fieldsDocument,
            Path indexPath,
            IOEngine ioEngine,
            DocumentGenerator<T> documentGenerator
    );
}

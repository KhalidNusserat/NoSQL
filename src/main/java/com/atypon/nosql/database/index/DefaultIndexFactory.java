package com.atypon.nosql.database.index;

import com.atypon.nosql.database.document.Document;
import com.atypon.nosql.database.document.DocumentFactory;
import com.atypon.nosql.database.io.IOEngine;

import java.nio.file.Path;

public class DefaultIndexFactory implements IndexFactory {
    @Override
    public Index createNewIndex(
            Document fieldsDocument,
            Path indexPath,
            IOEngine ioEngine
    ) {
        return new DefaultIndex(fieldsDocument, indexPath, ioEngine);
    }
}
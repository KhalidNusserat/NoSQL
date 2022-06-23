package com.atypon.nosql.index;

import com.atypon.nosql.document.DocumentFactory;
import com.atypon.nosql.io.IOEngine;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class DefaultIndexesCollectionFactory implements IndexesCollectionFactory {
    private final IOEngine ioEngine;

    private final DocumentFactory documentFactory;

    public DefaultIndexesCollectionFactory(IOEngine ioEngine, DocumentFactory documentFactory) {
        this.ioEngine = ioEngine;
        this.documentFactory = documentFactory;
    }

    @Override
    public IndexesCollection createIndexesCollection(Path indexesDirectory) {
        return new DefaultIndexesCollection(indexesDirectory, ioEngine, documentFactory);
    }
}

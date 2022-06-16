package com.atypon.nosql.database.index;

import com.atypon.nosql.database.document.Document;
import com.atypon.nosql.database.io.IOEngine;

import java.nio.file.Path;

public interface IndexFactory {
    Index createNewIndex(
            Document fieldsDocument,
            Path indexPath,
            IOEngine ioEngine
    );
}

package com.atypon.nosql.document;

import java.io.IOException;
import java.nio.file.Path;

public interface DocumentReader<DocumentValue> {
    Document<DocumentValue> read(Path path) throws IOException;
}

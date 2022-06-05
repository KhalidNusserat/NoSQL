package com.atypon.nosql.document;

import java.io.IOException;
import java.nio.file.Path;

public interface DocumentWriter<DocumentValue> {
    void write(Document<DocumentValue> document, Path path) throws IOException;
}

package com.atypon.nosql.document;

import java.io.IOException;

public interface DocumentReader<DocumentValue> {
    Document<DocumentValue> read(String path) throws IOException;
}

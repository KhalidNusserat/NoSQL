package com.atypon.nosql.document;

import java.io.IOException;

public interface DocumentWriter<DocumentValue> {
    void store(Document<DocumentValue> document, String directoryPath) throws IOException;
}

package com.atypon.nosql.document;

import java.io.IOException;

public interface DocumentWriter<DocumentValue> {
    void write(Document<DocumentValue> document, String path) throws IOException;

    void delete(String path) throws IOException;
}

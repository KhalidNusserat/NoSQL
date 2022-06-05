package com.atypon.nosql.document;

import com.google.common.base.Preconditions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SimpleDocumentWriter<DocumentValue> implements DocumentWriter<DocumentValue> {
    @Override
    public void store(Document<DocumentValue> document, String path) throws IOException {
        Preconditions.checkNotNull(document, path);
        Files.write(Path.of(path), document.getBytes());
    }

    @Override
    public void delete(String path) throws IOException {
        Preconditions.checkNotNull(path);
        Files.delete(Path.of(path));
    }
}

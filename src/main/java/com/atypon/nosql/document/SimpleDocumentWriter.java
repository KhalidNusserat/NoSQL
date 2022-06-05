package com.atypon.nosql.document;

import com.google.common.base.Preconditions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SimpleDocumentWriter<DocumentValue> implements DocumentWriter<DocumentValue> {
    @Override
    public void write(Document<DocumentValue> document, Path path) throws IOException {
        Preconditions.checkNotNull(document);
        Preconditions.checkNotNull(path);
        Files.write(path, document.getBytes());
    }

    @Override
    public void delete(Path path) throws IOException {
        Preconditions.checkNotNull(path);
        Files.delete(path);
    }
}

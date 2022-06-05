package com.atypon.nosql.document;

import com.google.common.base.Preconditions;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;

public class SimpleDocumentWriter<DocumentValue> implements DocumentWriter<DocumentValue> {
    @Override
    public void store(Document<DocumentValue> document, String path) throws IOException {
        Preconditions.checkNotNull(document, path);
        Files.write(document.getBytes(), new File(path));
    }
}

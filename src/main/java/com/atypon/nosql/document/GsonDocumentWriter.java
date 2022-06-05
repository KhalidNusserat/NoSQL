package com.atypon.nosql.document;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class GsonDocumentWriter<DocumentValue> implements DocumentWriter<DocumentValue> {
    @Override
    public void write(Document<DocumentValue> document, Path path) throws IOException {
        Preconditions.checkNotNull(document);
        Preconditions.checkNotNull(path);
        Gson gson = new Gson();
        gson.toJson(document, Files.newBufferedWriter(path));
    }
}

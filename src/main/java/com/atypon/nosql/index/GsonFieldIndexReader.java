package com.atypon.nosql.index;

import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class GsonFieldIndexReader implements FiledIndexReader {
    @Override
    public FieldIndex<?, ?> read(Path path) throws IOException {
        Gson gson = new Gson();
        return gson.fromJson(Files.newBufferedReader(path), FieldIndex.class);
    }
}

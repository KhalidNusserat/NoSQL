package com.atypon.nosql.index;

import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class GsonFieldIndexWriter implements FieldIndexWriter {
    @Override
    public void write(FieldIndex<?, ?> fieldIndex, Path path) throws IOException {
        Gson gson = new Gson();
        gson.toJson(fieldIndex, Files.newBufferedWriter(path));
    }
}

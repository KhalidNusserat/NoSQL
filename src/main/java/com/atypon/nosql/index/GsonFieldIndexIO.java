package com.atypon.nosql.index;

import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GsonFieldIndexIO implements FieldIndexIO {
    private final ExecutorService deleteService = Executors.newCachedThreadPool();

    @Override
    public void write(FieldIndex<?, ?> fieldIndex, Path path) throws IOException {
        Gson gson = new Gson();
        gson.toJson(fieldIndex, Files.newBufferedWriter(path));
    }

    @Override
    public void delete(Path path) {
        deleteService.submit(() -> Files.deleteIfExists(path));
    }

    @Override
    public FieldIndex<?, ?> read(Path path) throws IOException {
        Gson gson = new Gson();
        return gson.fromJson(Files.newBufferedReader(path), FieldIndex.class);
    }
}

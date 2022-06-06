package com.atypon.nosql.io;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GsonCopyOnWriteIO implements CopyOnWriteIO {
    private final ExecutorService deleteService = Executors.newCachedThreadPool();

    private final Gson gson = new Gson();

    @Override
    public <T> void write(T file, Path path) throws IOException {
        gson.toJson(file, Files.newBufferedWriter(path));
    }

    @Override
    public <T> T read(Path path, Class<T> tClass) throws IOException {
        return gson.fromJson(Files.newBufferedReader(path), tClass);
    }

    @Override
    public void delete(Path path) {
        deleteService.submit(() -> Files.deleteIfExists(path));
    }

    @Override
    public <T> Path update(T newFile, Path path) throws IOException {
        Path newPath = path.getParent().resolve(new Random().nextLong() + ".json");
        write(newFile, newPath);
        delete(path);
        return newPath;
    }
}

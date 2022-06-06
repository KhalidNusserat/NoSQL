package com.atypon.nosql.io;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GsonCopyOnWriteIO<T> implements CopyOnWriteIO<T> {
    private final ExecutorService deleteService = Executors.newCachedThreadPool();

    private final Gson gson = new Gson();

    private final Random random = new Random();

    @Override
    public Path write(T file, Path directory, String extension) throws IOException {
        Path filepath = directory.resolve(random.nextLong() + extension);
        try (BufferedWriter writer = Files.newBufferedWriter(filepath)) {
            gson.toJson(file, writer);
        }
        return filepath;
    }

    @Override
    public T read(Path filepath, Type type) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(filepath)) {
            return gson.fromJson(reader, type);
        }
    }

    @Override
    public void delete(Path path) {
        deleteService.submit(() -> Files.deleteIfExists(path));
    }

    @Override
    public Path update(T newFile, Path filepath, String extension) throws IOException {
        Path newFilepath = write(newFile, filepath.getParent(), extension);
        delete(filepath);
        return newFilepath;
    }
}

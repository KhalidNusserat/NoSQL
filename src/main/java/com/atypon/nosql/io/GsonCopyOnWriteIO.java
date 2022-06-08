package com.atypon.nosql.io;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GsonCopyOnWriteIO implements CopyOnWriteIO {
    private final ExecutorService deleteService = Executors.newCachedThreadPool();

    private final Gson gson = new Gson();

    private final Random random = new Random();

    private int Attempts = 5;

    private <T> Optional<T> read(Path filepath, Type type, int remainingAttempts) {
        if (remainingAttempts == 0) {
            return Optional.empty();
        }
        try (BufferedReader reader = Files.newBufferedReader(filepath)) {
            return Optional.of(gson.fromJson(reader, type));
        } catch (IOException e) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                throw new RuntimeException("Interrupted while reading a file: " + filepath);
            }
            return read(filepath, type, remainingAttempts - 1);
        }
    }

    @Override
    public <T> Optional<T> read(Path filepath, Type type) {
        return read(filepath, type, Attempts);
    }

    @Override
    public <T> Path write(T file, Type type, Path directory, String extension) throws IOException {
        Path filepath = directory.resolve(random.nextLong() + extension);
        try (BufferedWriter writer = Files.newBufferedWriter(filepath)) {
            gson.toJson(file, type, writer);
        }
        return filepath;
    }

    @Override
    public void delete(Path path) {
        deleteService.submit(() -> Files.deleteIfExists(path));
    }

    @Override
    public <T> Path update(T newFile, Type type, Path filepath, String extension) throws IOException {
        Path newFilepath = write(newFile, type, filepath.getParent(), extension);
        delete(filepath);
        return newFilepath;
    }

    public int getAttempts() {
        return Attempts;
    }

    public void setAttempts(int attempts) {
        Attempts = attempts;
    }
}

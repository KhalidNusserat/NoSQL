package com.atypon.nosql.gsondocument;

import com.atypon.nosql.io.DocumentsIO;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class GsonDocumentsIO implements DocumentsIO<GsonDocument> {
    private final ExecutorService deleteService = Executors.newCachedThreadPool();

    private final Random random = new Random();

    private final Gson gson;

    private final int ATTEMPTS = 5;

    private final int RETRY_DELAY = 10;

    public GsonDocumentsIO(Gson gson) {
        this.gson = gson;
    }

    private GsonDocument parse(String src) {
        JsonObject object = gson.fromJson(src, JsonObject.class);
        GsonDocument document = new GsonDocument(object);
        document.object.addProperty("_id", object.get("_id").getAsString());
        return document;
    }

    private Optional<GsonDocument> read(Path documentPath, int remainingAttempts) {
        if (remainingAttempts == 0) {
            return Optional.empty();
        }
        try (BufferedReader reader = Files.newBufferedReader(documentPath)) {
            return Optional.of(parse(reader.lines().collect(Collectors.joining())));
        } catch (IOException e) {
            try {
                Thread.sleep(RETRY_DELAY);
            } catch (InterruptedException ex) {
                throw new RuntimeException("Interrupted while reading a file: " + documentPath);
            }
            return read(documentPath, remainingAttempts - 1);
        }
    }

    @Override
    public Optional<GsonDocument> read(Path documentPath) {
        return read(documentPath, ATTEMPTS);
    }

    @Override
    public Path write(GsonDocument document, Path directoryPath) throws IOException {
        Path filepath = directoryPath.resolve(random.nextLong() + ".json");
        try (BufferedWriter writer = Files.newBufferedWriter(filepath)) {
            writer.write(document.toString());
        }
        return filepath;
    }

    @Override
    public void delete(Path path) {
        deleteService.submit(() -> Files.deleteIfExists(path));
    }

    @Override
    public Path update(GsonDocument newDocument, Path documentPath) throws IOException {
        Path newFilepath = write(newDocument, documentPath.getParent());
        delete(documentPath);
        return newFilepath;
    }

    @Override
    public Collection<GsonDocument> readDirectory(Path directoryPath) {
        try {
            return Files.walk(directoryPath, 1)
                    .map(this::read)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("Couldn't access the directory: " + directoryPath);
        }
    }
}

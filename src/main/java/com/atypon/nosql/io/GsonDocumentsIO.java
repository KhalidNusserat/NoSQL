package com.atypon.nosql.io;

import com.atypon.nosql.document.DocumentParser;
import com.atypon.nosql.gsondocument.GsonDocument;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Type;
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

    private final Gson gson = new Gson();

    private final Random random = new Random();

    private int attempts = 5;

    private int retryDelay = 10;

    private final DocumentParser<GsonDocument> documentParser;

    public GsonDocumentsIO(DocumentParser<GsonDocument> documentParser) {
        this.documentParser = documentParser;
    }

    private Optional<GsonDocument> read(Path documentPath, int remainingAttempts) {
        if (remainingAttempts == 0) {
            return Optional.empty();
        }
        try (BufferedReader reader = Files.newBufferedReader(documentPath)) {
            return Optional.of(documentParser.parse(reader.lines().collect(Collectors.joining())));
        } catch (IOException e) {
            try {
                Thread.sleep(retryDelay);
            } catch (InterruptedException ex) {
                throw new RuntimeException("Interrupted while reading a file: " + documentPath);
            }
            return read(documentPath, remainingAttempts - 1);
        }
    }

    @Override
    public Optional<GsonDocument> read(Path documentPath) {
        return read(documentPath, attempts);
    }

    @Override
    public Path write(GsonDocument document, Path directoryPath) throws IOException {
        Path filepath = directoryPath.resolve(random.nextLong() + ".json");
        try (BufferedWriter writer = Files.newBufferedWriter(filepath)) {
            gson.toJson(document.toString());
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
    public Collection<GsonDocument> readAll(Path directoryPath) {
        try {
            return Files.walk(directoryPath)
                    .map(this::read)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("Couldn't access the directory: " + directoryPath);
        }
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public int getRetryDelay() {
        return retryDelay;
    }

    public void setRetryDelay(int retryDelay) {
        this.retryDelay = retryDelay;
    }
}

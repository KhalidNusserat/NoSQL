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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class GsonDocumentsIO implements DocumentsIO<GsonDocument> {
    private final ExecutorService deleteService = Executors.newCachedThreadPool();

    private final Gson gson = new Gson();

    private final Random random = new Random();

    private int attempts = 5;

    private int retryDelay = 10;

    private final DocumentParser<GsonDocument> documentParser;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

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
        lock.writeLock().lock();
        Path filepath = directoryPath.resolve(random.nextLong() + ".json");
        try (BufferedWriter writer = Files.newBufferedWriter(filepath)) {
            writer.write(document.toString());
        } finally {
            lock.writeLock().unlock();
        }
        return filepath;
    }

    @Override
    public void delete(Path path) {
        lock.writeLock().lock();
        deleteService.submit(() -> Files.deleteIfExists(path));
        lock.writeLock().unlock();
    }

    @Override
    public Path update(GsonDocument newDocument, Path documentPath) throws IOException {
        lock.writeLock().lock();
        Path newFilepath = write(newDocument, documentPath.getParent());
        delete(documentPath);
        lock.writeLock().unlock();
        return newFilepath;
    }

    @Override
    public Collection<GsonDocument> readAll(Path directoryPath) {
        lock.readLock().lock();
        try {
            return Files.walk(directoryPath)
                    .map(this::read)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("Couldn't access the directory: " + directoryPath);
        } finally {
            lock.readLock().unlock();
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

package com.atypon.nosql.io;

import com.atypon.nosql.document.Document;
import com.atypon.nosql.document.DocumentGenerator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class DefaultIOEngine implements IOEngine {
    private final ExecutorService deleteService = Executors.newCachedThreadPool();

    private final Set<Path> uncommittedFiles = new HashSet<>();

    private final Random random = new Random();

    private final int ATTEMPTS = 5;

    private final int RETRY_DELAY = 10;

    @Override
    public Path write(Document<?> document, Path directoryPath) throws IOException {
        Path documentPath = getNewDocumentPath(directoryPath);
        writeAtPath(document, documentPath);
        return documentPath;
    }

    private Path getNewDocumentPath(Path directoryPath) {
        return directoryPath.resolve(random.nextLong() + ".json");
    }

    private void writeAtPath(Document<?> document, Path documentPath) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(documentPath)) {
            writer.write(document.toString());
        }
    }

    @Override
    public void delete(Path path) {
        deleteService.submit(() -> Files.deleteIfExists(path));
    }

    @Override
    public Path update(Document<?> updatedDocument, Path documentPath) throws IOException {
        Path updatedDocumentPath = getNewDocumentPath(documentPath.getParent());
        add(updatedDocumentPath);
        writeAtPath(updatedDocument, updatedDocumentPath);
        commit(updatedDocumentPath);
        delete(documentPath);
        return updatedDocumentPath;
    }

    private void add(Path path) {
        uncommittedFiles.add(path);
    }

    private void commit(Path path) {
        uncommittedFiles.remove(path);
    }

    private <T extends Document<?>> Optional<T> read(
            Path documentPath,
            DocumentGenerator<T> documentGenerator,
            int remainingAttempts
    ) {
        if (remainingAttempts == 0) {
            return Optional.empty();
        }
        try (BufferedReader reader = Files.newBufferedReader(documentPath)) {
            String src = reader.lines().collect(Collectors.joining());
            return Optional.of(documentGenerator.createFromString(src));
        } catch (IOException e) {
            try {
                Thread.sleep(RETRY_DELAY);
            } catch (InterruptedException ex) {
                throw new RuntimeException("Interrupted while reading a file: " + documentPath);
            }
            return read(documentPath, documentGenerator, remainingAttempts - 1);
        }
    }

    @Override
    public <T extends Document<?>> Optional<T> read(Path documentPath, DocumentGenerator<T> documentGenerator) {
        if (uncommittedFiles.contains(documentPath)) {
            return Optional.empty();
        }
        return read(documentPath, documentGenerator, ATTEMPTS);
    }

    @Override
    public <T extends Document<?>> Collection<T> readDirectory(
            Path directoryPath,
            DocumentGenerator<T> documentGenerator
    ) {
        try {
            return Files.walk(directoryPath, 1)
                    .map(documentPath -> read(documentPath, documentGenerator))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("Couldn't access the directory: " + directoryPath);
        }
    }
}

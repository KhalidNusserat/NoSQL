package com.atypon.nosql.database.io;

import com.atypon.nosql.database.document.Document;
import com.atypon.nosql.database.document.DocumentGenerator;
import com.atypon.nosql.database.utils.ExtraFileUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class DefaultIOEngine<T extends Document<?>> implements IOEngine<T> {
    private final ExecutorService deleteService = Executors.newCachedThreadPool();

    private final Set<Path> uncommittedFiles = new HashSet<>();

    private final Random random = new Random();

    @Override
    public Path write(T document, Path directoryPath) {
        Path documentPath = getNewDocumentPath(directoryPath);
        writeAtPath(document, documentPath);
        return directoryPath;
    }

    private Path getNewDocumentPath(Path directoryPath) {
        return directoryPath.resolve(random.nextLong() + ".json");
    }

    private void writeAtPath(T document, Path documentPath) {
        try (BufferedWriter writer = Files.newBufferedWriter(documentPath)) {
            writer.write(document.toString());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void delete(Path path) {
        deleteService.submit(() -> Files.deleteIfExists(path));
    }

    @Override
    public Path update(T updatedDocument, Path documentPath) {
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

    @Override
    public Optional<T> read(Path documentPath, DocumentGenerator<T> documentGenerator) {
        if (uncommittedFiles.contains(documentPath)) {
            return Optional.empty();
        }
        try (BufferedReader reader = Files.newBufferedReader(documentPath)) {
            String src = reader.lines().collect(Collectors.joining());
            return Optional.of(documentGenerator.createFromString(src));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<T> readDirectory(Path directoryPath, DocumentGenerator<T> documentGenerator) {
        return ExtraFileUtils.traverseDirectory(directoryPath)
                .filter(ExtraFileUtils::isJsonFile)
                .map(documentPath -> read(documentPath, documentGenerator))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }
}

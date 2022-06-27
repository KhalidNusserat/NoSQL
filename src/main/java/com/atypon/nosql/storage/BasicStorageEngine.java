package com.atypon.nosql.storage;

import com.atypon.nosql.collection.Stored;
import com.atypon.nosql.document.Document;
import com.atypon.nosql.document.DocumentFactory;
import com.atypon.nosql.utils.FileUtils;

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

public class BasicStorageEngine implements StorageEngine {
    private final ExecutorService deleteService = Executors.newCachedThreadPool();

    private final Set<Path> uncommittedFiles = new HashSet<>();

    private final Random random = new Random();

    private final DocumentFactory documentFactory;

    public BasicStorageEngine(DocumentFactory documentFactory) {
        this.documentFactory = documentFactory;
    }

    @Override
    public Stored<Document> writeDocument(Document document, Path directoryPath) {
        Path documentPath = getNewFilePath(directoryPath);
        writeDocumentAtPath(document, documentPath);
        return Stored.createStoredObject(document, documentPath);
    }

    private Path getNewFilePath(Path directoryPath) {
        return directoryPath.resolve(random.nextLong() + ".json");
    }

    private void writeDocumentAtPath(Document document, Path documentPath) {
        try (BufferedWriter writer = Files.newBufferedWriter(documentPath)) {
            writer.write(document.toString());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void deleteFile(Path path) {
        deleteService.submit(() -> Files.deleteIfExists(path));
    }

    @Override
    public Stored<Document> updateDocument(Document update, Path documentPath) {
        Path updatedDocumentPath = getNewFilePath(documentPath.getParent());
        add(updatedDocumentPath);
        Document oldDocument = readDocument(documentPath).orElseThrow();
        Document updatedDocument = oldDocument.overrideFields(update);
        writeDocumentAtPath(updatedDocument, updatedDocumentPath);
        commit(updatedDocumentPath);
        deleteFile(documentPath);
        return Stored.createStoredObject(updatedDocument, updatedDocumentPath);
    }

    private void add(Path path) {
        uncommittedFiles.add(path);
    }

    private void commit(Path path) {
        uncommittedFiles.remove(path);
    }

    @Override
    public Optional<Document> readDocument(Path documentPath) {
        if (uncommittedFiles.contains(documentPath)) {
            return Optional.empty();
        }
        try (BufferedReader reader = Files.newBufferedReader(documentPath)) {
            String src = reader.lines().collect(Collectors.joining());
            return Optional.of(documentFactory.createFromJson(src));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Document> readDocumentsDirectory(Path directoryPath) {
        return FileUtils.traverseDirectory(directoryPath)
                .filter(FileUtils::isJsonFile)
                .map(this::readDocument)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }
}

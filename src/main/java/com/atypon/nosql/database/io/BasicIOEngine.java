package com.atypon.nosql.database.io;

import com.atypon.nosql.database.collection.StoredDocument;
import com.atypon.nosql.database.document.Document;
import com.atypon.nosql.database.document.DocumentFactory;
import com.atypon.nosql.database.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;

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

@Slf4j
public class BasicIOEngine implements IOEngine {
    private final ExecutorService deleteService = Executors.newCachedThreadPool();

    private final Set<Path> uncommittedFiles = new HashSet<>();

    private final Random random = new Random();

    private final DocumentFactory documentFactory;

    public BasicIOEngine(DocumentFactory documentFactory) {
        this.documentFactory = documentFactory;
    }

    @Override
    public StoredDocument write(Document document, Path directoryPath) {
        Path documentPath = getNewDocumentPath(directoryPath);
        writeAtPath(document, documentPath);
        return StoredDocument.createStoredDocument(document, documentPath);
    }

    private Path getNewDocumentPath(Path directoryPath) {
        return directoryPath.resolve(random.nextLong() + ".json");
    }

    private void writeAtPath(Document document, Path documentPath) {
        try (BufferedWriter writer = Files.newBufferedWriter(documentPath)) {
            writer.write(document.toString());
        } catch (IOException e) {
            log.error(
                    "An error occurred while trying to write the document \"{}\" at \"{}\": {}",
                    document,
                    documentPath,
                    e.getMessage()
            );
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void delete(Path path) {
        deleteService.submit(() -> Files.deleteIfExists(path));
    }

    @Override
    public StoredDocument update(Document update, Path documentPath) {
        Path updatedDocumentPath = getNewDocumentPath(documentPath.getParent());
        add(updatedDocumentPath);
        Document oldDocument = read(documentPath).orElseThrow();
        Document updatedDocument = oldDocument.overrideFields(update);
        writeAtPath(updatedDocument, updatedDocumentPath);
        commit(updatedDocumentPath);
        delete(documentPath);
        return StoredDocument.createStoredDocument(updatedDocument, updatedDocumentPath);
    }

    private void add(Path path) {
        uncommittedFiles.add(path);
    }

    private void commit(Path path) {
        uncommittedFiles.remove(path);
    }

    @Override
    public Optional<Document> read(Path documentPath) {
        if (uncommittedFiles.contains(documentPath)) {
            return Optional.empty();
        }
        try (BufferedReader reader = Files.newBufferedReader(documentPath)) {
            String src = reader.lines().collect(Collectors.joining());
            return Optional.of(documentFactory.createFromString(src));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Document> readDirectory(Path directoryPath) {
        return FileUtils.traverseDirectory(directoryPath)
                .filter(FileUtils::isJsonFile)
                .map(this::read)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }
}

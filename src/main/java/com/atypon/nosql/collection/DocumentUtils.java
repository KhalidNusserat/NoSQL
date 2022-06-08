package com.atypon.nosql.collection;

import com.atypon.nosql.document.Document;
import com.atypon.nosql.document.DocumentParser;
import com.atypon.nosql.io.CopyOnWriteIO;
import com.atypon.nosql.utils.ExtraFileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class DocumentUtils<E, T extends Document<E>> {
    private final Path directoryPath;

    private final DocumentParser<T> parser;

    private final CopyOnWriteIO io;

    public DocumentUtils(Path directoryPath, DocumentParser<T> parser, CopyOnWriteIO io) {
        this.directoryPath = directoryPath;
        this.parser = parser;
        this.io = io;
    }

    public Optional<T> readDocument(Path path) {
        Optional<String> src = io.read(path, String.class);
        return src.map(parser::parse);
    }

    public List<Path> getPaths(T matchDocument) throws IOException {
        return Files.walk(directoryPath)
                .filter(ExtraFileUtils::isJsonFile)
                .filter(path -> {
                    Optional<T> parsedDocument = readDocument(path);
                    if (parsedDocument.isEmpty()) {
                        return false;
                    } else {
                        return parsedDocument.get().matches(matchDocument);
                    }
                })
                .toList();
    }

    public Collection<T> getAll() throws IOException {
        return Files.walk(directoryPath)
                .filter(ExtraFileUtils::isJsonFile)
                .map(this::readDocument)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    public Collection<T> getAllThatMatches(T matchDocument) throws IOException {
        return getAll().stream().filter(document -> document.matches(matchDocument)).toList();
    }

    public boolean contains(T matchDocument) throws IOException {
        return !getAllThatMatches(matchDocument).isEmpty();
    }
}
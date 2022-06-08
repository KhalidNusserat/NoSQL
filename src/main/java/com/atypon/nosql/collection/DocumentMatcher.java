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

public class DocumentMatcher<E, T extends Document<E>> {
    private final Path directoryPath;

    private final DocumentParser<T> parser;

    private final CopyOnWriteIO io;

    public DocumentMatcher(Path directoryPath, DocumentParser<T> parser, CopyOnWriteIO io) {
        this.directoryPath = directoryPath;
        this.parser = parser;
        this.io = io;
    }

    public List<Path> getPaths(T matchDocument) throws IOException {
        return Files.walk(directoryPath)
                .filter(ExtraFileUtils::isJsonFile)
                .filter(path -> {
                    try {
                        T parsedDocument = parser.parse(io.read(path, String.class));
                        return parsedDocument.matches(matchDocument);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();
    }

    public Collection<T> getAll() throws IOException {
        return Files.walk(directoryPath)
                .filter(ExtraFileUtils::isJsonFile)
                .map(filepath -> {
                    try {
                        return parser.parse(io.read(filepath, String.class));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();
    }

    public Collection<T> getAll(T matchDocument) throws IOException {
        return getAll().stream().filter(document -> document.matches(matchDocument)).toList();
    }

    public boolean contains(T matchDocument) throws IOException {
        return !getAll(matchDocument).isEmpty();
    }
}
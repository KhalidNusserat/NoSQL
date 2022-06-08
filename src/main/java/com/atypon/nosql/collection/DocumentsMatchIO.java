package com.atypon.nosql.collection;

import com.atypon.nosql.document.Document;
import com.atypon.nosql.io.DocumentsIO;
import com.atypon.nosql.utils.ExtraFileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class DocumentsMatchIO<E, T extends Document<E>> {
    private final DocumentsIO<T> io;

    public DocumentsMatchIO(DocumentsIO<T> io) {
        this.io = io;
    }

    public List<Path> getPaths(T matchDocument, Path directoryPath) throws IOException {
        return Files.walk(directoryPath)
                .filter(ExtraFileUtils::isJsonFile)
                .filter(path -> {
                    Optional<T> parsedDocument = io.read(path);
                    if (parsedDocument.isEmpty()) {
                        return false;
                    } else {
                        return parsedDocument.get().matches(matchDocument);
                    }
                })
                .toList();
    }

    public Collection<T> getAllThatMatches(T matchDocument, Path directoryPath) throws IOException {
        return Files.walk(directoryPath)
                .filter(ExtraFileUtils::isJsonFile)
                .map(io::read)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(document -> document.matches(matchDocument))
                .toList();
    }

    public boolean contains(T matchDocument, Path directoryPath) throws IOException {
        return Files.walk(directoryPath)
                .filter(ExtraFileUtils::isJsonFile)
                .map(io::read)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .anyMatch(document -> document.matches(matchDocument));
    }
}
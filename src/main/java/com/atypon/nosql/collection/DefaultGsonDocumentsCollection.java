package com.atypon.nosql.collection;

import com.atypon.nosql.gsondocument.GsonDocument;
import com.atypon.nosql.io.DocumentsIO;
import com.atypon.nosql.utils.ExtraFileUtils;

import javax.naming.directory.SchemaViolationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class DefaultGsonDocumentsCollection implements DocumentsCollection<GsonDocument> {
    private final DocumentsIO<GsonDocument> documentsIO;

    private final Path directoryPath;

    public DefaultGsonDocumentsCollection(DocumentsIO<GsonDocument> documentsIO, Path directoryPath) {
        this.documentsIO = documentsIO;
        this.directoryPath = directoryPath;
        try {
            Files.createDirectories(directoryPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean contains(GsonDocument matchDocument) {
        try {
            return Files.walk(directoryPath, 1)
                    .filter(ExtraFileUtils::isJsonFile)
                    .map(documentsIO::read)
                    .filter(Optional::isPresent)
                    .anyMatch(document -> document.get().matches(matchDocument));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<GsonDocument> getAllThatMatches(GsonDocument matchDocument) {
        try {
            return Files.walk(directoryPath, 1)
                    .filter(ExtraFileUtils::isJsonFile)
                    .map(documentsIO::read)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .filter(document -> document.matches(matchDocument))
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<GsonDocument> getAll() {
        return documentsIO.readDirectory(directoryPath);
    }

    @Override
    public Path put(GsonDocument document) throws IOException {
        List<Path> paths = getPathsThatMatch((GsonDocument) document.matchID());
        if (paths.size() == 1) {
            return documentsIO.update(document, paths.get(0));
        } else if (paths.size() == 0) {
            return documentsIO.write(document, directoryPath);
        } else {
            throw new IllegalStateException("There are multiple files with the same ObjectID");
        }
    }

    @Override
    public void deleteAllThatMatches(GsonDocument matchDocument) throws IOException {
        List<Path> paths = getPathsThatMatch(matchDocument);
        for (Path path : paths) {
            documentsIO.delete(path);
        }
    }

    private List<Path> getPathsThatMatch(GsonDocument matchDocument) {
        try {
            return Files.walk(directoryPath, 1)
                    .filter(ExtraFileUtils::isJsonFile)
                    .filter(path -> documentsIO.read(path)
                            .map(document -> document.matches(matchDocument))
                            .orElse(false))
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
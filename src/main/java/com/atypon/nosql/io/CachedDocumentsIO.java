package com.atypon.nosql.io;

import com.atypon.nosql.cache.Cache;
import com.atypon.nosql.document.Document;
import com.atypon.nosql.utils.ExtraFileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;

public class CachedDocumentsIO<T extends Document<?>> implements DocumentsIO<T> {
    private final DocumentsIO<T> documentsIO;

    private final Cache<Path, T> cache;

    public CachedDocumentsIO(DocumentsIO<T> documentsIO, Cache<Path, T> cache) {
        this.documentsIO = documentsIO;
        this.cache = cache;
    }

    public static <T extends Document<?>> CachedDocumentsIO<T> from(DocumentsIO<T> documentsIO, Cache<Path, T> cache) {
        return new CachedDocumentsIO<>(documentsIO, cache);
    }

    @Override
    public Path write(T document, Path directory) throws IOException {
        Path filepath = documentsIO.write(document, directory);
        cache.put(filepath, document);
        return filepath;
    }

    @Override
    public Optional<T> read(Path documentPath) {
        Optional<T> cacheResult = cache.get(documentPath);
        if (cacheResult.isPresent()) {
            return cacheResult;
        } else {
            return documentsIO.read(documentPath);
        }
    }

    @Override
    public void delete(Path documentPath) {
        cache.remove(documentPath);
        documentsIO.delete(documentPath);
    }

    @Override
    public Path update(T newDocument, Path documentPath) throws IOException {
        Path filepath = documentsIO.update(newDocument, documentPath);
        cache.put(filepath, newDocument);
        return filepath;
    }

    @Override
    public Collection<T> readAll(Path directoryPath) {
        try {
            return Files.walk(directoryPath)
                    .filter(ExtraFileUtils::isJsonFile)
                    .map(path -> cache.get(path).orElse(documentsIO.read(path).orElseThrow()))
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("Interrupted while reading all documents in the directory: " + directoryPath);
        }
    }
}

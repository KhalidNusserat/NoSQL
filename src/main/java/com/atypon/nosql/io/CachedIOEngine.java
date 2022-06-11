package com.atypon.nosql.io;

import com.atypon.nosql.cache.Cache;
import com.atypon.nosql.document.Document;
import com.atypon.nosql.document.DocumentGenerator;
import com.atypon.nosql.utils.ExtraFileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CachedIOEngine implements IOEngine {
    private final IOEngine ioEngine;

    private final Cache<Path, Document<?>> cache;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public CachedIOEngine(IOEngine ioEngine, Cache<Path, Document<?>> cache) {
        this.ioEngine = ioEngine;
        this.cache = cache;
    }

    public static CachedIOEngine from(IOEngine IOEngine, Cache<Path, Document<?>> cache) {
        return new CachedIOEngine(IOEngine, cache);
    }

    @Override
    public Path write(Document<?> document, Path directory) throws IOException {
        Path filepath = ioEngine.write(document, directory);
        cache.put(filepath, document);
        return filepath;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Document<?>> Optional<T> read(Path documentPath, DocumentGenerator<T> documentGenerator) {
        Optional<?> cacheResult = cache.get(documentPath);
        if (cacheResult.isPresent()) {
            return (Optional<T>) cacheResult;
        } else {
            return ioEngine.read(documentPath, documentGenerator);
        }
    }

    @Override
    public void delete(Path documentPath) {
        ioEngine.delete(documentPath);
    }

    @Override
    public Path update(Document<?> updatedDocument, Path documentPath) throws IOException {
        Path filepath = ioEngine.update(updatedDocument, documentPath);
        cache.put(filepath, updatedDocument);
        return filepath;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Document<?>> Collection<T> readDirectory(
            Path directoryPath,
            DocumentGenerator<T> documentGenerator
    ) {
        try {
            return Files.walk(directoryPath, 1)
                    .filter(ExtraFileUtils::isJsonFile)
                    .map(path -> (Optional<T>) cache.get(path))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

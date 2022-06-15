package com.atypon.nosql.database.io;

import com.atypon.nosql.database.cache.Cache;
import com.atypon.nosql.database.document.Document;
import com.atypon.nosql.database.document.DocumentGenerator;
import com.atypon.nosql.database.utils.ExtraFileUtils;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class CachedIOEngine<T extends Document> implements IOEngine<T> {
    private final IOEngine<T> ioEngine;

    private final Cache<Path, T> cache;

    private CachedIOEngine(IOEngine<T> ioEngine, Cache<Path, T> cache) {
        this.ioEngine = ioEngine;
        this.cache = cache;
    }

    public static <T extends Document> CachedIOEngine<T> from(IOEngine<T> IOEngine, Cache<Path, T> cache) {
        return new CachedIOEngine<>(IOEngine, cache);
    }

    @Override
    public Path write(T document, Path directory) {
        Path filepath = ioEngine.write(document, directory);
        cache.put(filepath, document);
        return filepath;
    }

    @Override
    public Optional<T> read(Path documentPath, DocumentGenerator<T> documentGenerator) {
        Optional<T> cacheResult = cache.get(documentPath);
        if (cacheResult.isPresent()) {
            return cacheResult;
        } else {
            return ioEngine.read(documentPath, documentGenerator);
        }
    }

    @Override
    public void delete(Path documentPath) {
        ioEngine.delete(documentPath);
    }

    @Override
    public Path update(T updatedDocument, Path documentPath) {
        Path filepath = ioEngine.update(updatedDocument, documentPath);
        cache.put(filepath, updatedDocument);
        return filepath;
    }

    @Override
    public List<T> readDirectory(Path directoryPath, DocumentGenerator<T> documentGenerator) {
        return ExtraFileUtils.traverseDirectory(directoryPath)
                .filter(ExtraFileUtils::isJsonFile)
                .map(path -> read(path, documentGenerator))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }
}

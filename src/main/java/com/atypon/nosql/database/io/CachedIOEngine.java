package com.atypon.nosql.database.io;

import com.atypon.nosql.database.cache.Cache;
import com.atypon.nosql.database.document.Document;
import com.atypon.nosql.database.document.DocumentGenerator;
import com.atypon.nosql.database.utils.FileUtils;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class CachedIOEngine implements IOEngine {
    private final IOEngine ioEngine;

    private final Cache<Path, Document> cache;

    private CachedIOEngine(IOEngine ioEngine, Cache<Path, Document> cache) {
        this.ioEngine = ioEngine;
        this.cache = cache;
    }

    public static CachedIOEngine from(IOEngine IOEngine, Cache<Path, Document> cache) {
        return new CachedIOEngine(IOEngine, cache);
    }

    @Override
    public Path write(Document document, Path directory) {
        Path filepath = ioEngine.write(document, directory);
        cache.put(filepath, document);
        return filepath;
    }

    @Override
    public Optional<Document> read(Path documentPath) {
        Optional<Document> cacheResult = cache.get(documentPath);
        if (cacheResult.isPresent()) {
            return cacheResult;
        } else {
            return ioEngine.read(documentPath);
        }
    }

    @Override
    public void delete(Path documentPath) {
        ioEngine.delete(documentPath);
    }

    @Override
    public Path update(Document updatedDocument, Path documentPath) {
        Path filepath = ioEngine.update(updatedDocument, documentPath);
        cache.put(filepath, updatedDocument);
        return filepath;
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

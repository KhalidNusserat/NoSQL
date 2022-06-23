package com.atypon.nosql.io;

import com.atypon.nosql.cache.Cache;
import com.atypon.nosql.collection.StoredDocument;
import com.atypon.nosql.document.Document;
import com.atypon.nosql.utils.FileUtils;

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
    public StoredDocument write(Document document, Path directory) {
        StoredDocument storedDocument = ioEngine.write(document, directory);
        cache.put(storedDocument.path(), document);
        return storedDocument;
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
    public StoredDocument update(Document updatedDocument, Path documentPath) {
        StoredDocument storedDocument = ioEngine.update(updatedDocument, documentPath);
        cache.put(storedDocument.path(), updatedDocument);
        return storedDocument;
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

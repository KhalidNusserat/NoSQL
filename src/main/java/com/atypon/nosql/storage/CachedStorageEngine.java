package com.atypon.nosql.storage;

import com.atypon.nosql.cache.Cache;
import com.atypon.nosql.collection.StoredDocument;
import com.atypon.nosql.document.Document;
import com.atypon.nosql.utils.FileUtils;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class CachedStorageEngine implements StorageEngine {
    private final StorageEngine storageEngine;

    private final Cache<Path, Document> cache;

    private CachedStorageEngine(StorageEngine storageEngine, Cache<Path, Document> cache) {
        this.storageEngine = storageEngine;
        this.cache = cache;
    }

    public static CachedStorageEngine from(StorageEngine StorageEngine, Cache<Path, Document> cache) {
        return new CachedStorageEngine(StorageEngine, cache);
    }

    @Override
    public StoredDocument write(Document document, Path directory) {
        StoredDocument storedDocument = storageEngine.write(document, directory);
        cache.put(storedDocument.path(), document);
        return storedDocument;
    }

    @Override
    public Optional<Document> read(Path documentPath) {
        Optional<Document> cacheResult = cache.get(documentPath);
        if (cacheResult.isPresent()) {
            return cacheResult;
        } else {
            return storageEngine.read(documentPath);
        }
    }

    @Override
    public void delete(Path documentPath) {
        storageEngine.delete(documentPath);
    }

    @Override
    public StoredDocument update(Document updatedDocument, Path documentPath) {
        StoredDocument storedDocument = storageEngine.update(updatedDocument, documentPath);
        cache.put(storedDocument.path(), storedDocument.document());
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

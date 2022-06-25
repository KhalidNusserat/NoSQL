package com.atypon.nosql.storage;

import com.atypon.nosql.cache.Cache;
import com.atypon.nosql.collection.Stored;
import com.atypon.nosql.document.Document;
import com.atypon.nosql.index.Index;
import com.atypon.nosql.utils.FileUtils;
import lombok.Builder;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Builder
public class CachedStorageEngine implements StorageEngine {
    private final StorageEngine storageEngine;

    private final Cache<Path, Document> documentCache;

    private final Cache<Path, Index> indexCache;

    private CachedStorageEngine(
            StorageEngine storageEngine,
            Cache<Path, Document> documentCache,
            Cache<Path, Index> indexCache) {
        this.storageEngine = storageEngine;
        this.documentCache = documentCache;
        this.indexCache = indexCache;
    }

    @Override
    public Stored<Document> writeDocument(Document document, Path directory) {
        Stored<Document> stored = storageEngine.writeDocument(document, directory);
        documentCache.put(stored.path(), document);
        return stored;
    }

    @Override
    public Optional<Document> readDocument(Path documentPath) {
        Optional<Document> cacheResult = documentCache.get(documentPath);
        if (cacheResult.isPresent()) {
            return cacheResult;
        } else {
            return storageEngine.readDocument(documentPath);
        }
    }

    @Override
    public void deleteFile(Path documentPath) {
        storageEngine.deleteFile(documentPath);
    }

    @Override
    public Stored<Document> updateDocument(Document updatedDocument, Path documentPath) {
        Stored<Document> stored = storageEngine.updateDocument(updatedDocument, documentPath);
        documentCache.put(stored.path(), stored.object());
        return stored;
    }

    @Override
    public List<Document> readDocumentsDirectory(Path directoryPath) {
        return FileUtils.traverseDirectory(directoryPath)
                .filter(FileUtils::isJsonFile)
                .map(this::readDocument)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }
}

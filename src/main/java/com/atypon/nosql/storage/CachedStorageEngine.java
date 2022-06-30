package com.atypon.nosql.storage;

import com.atypon.nosql.cache.Cache;
import com.atypon.nosql.document.Document;
import com.atypon.nosql.utils.FileUtils;
import com.atypon.nosql.utils.Stored;
import lombok.Builder;
import lombok.ToString;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@ToString
@Builder
public class CachedStorageEngine implements StorageEngine {

    private final StorageEngine storageEngine;

    private final Cache<Path, Document> documentCache;

    private CachedStorageEngine(StorageEngine storageEngine, Cache<Path, Document> documentCache) {
        this.storageEngine = storageEngine;
        this.documentCache = documentCache;
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
            Optional<Document> optionalDocument = storageEngine.readDocument(documentPath);
            optionalDocument.ifPresent(document -> documentCache.put(documentPath, document));
            return optionalDocument;
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

package com.atypon.nosql.storage;

import com.atypon.nosql.cache.Cache;
import com.atypon.nosql.document.Document;

import java.nio.file.Path;

public abstract class StorageEngines {

    public static BasicStorageEngine basic() {
        return new BasicStorageEngine();
    }

    public static CachedStorageEngine cached(StorageEngine storageEngine, Cache<Path, Document> documentCache) {
        return CachedStorageEngine.builder()
                .storageEngine(storageEngine)
                .documentCache(documentCache)
                .build();
    }

    public static SecureStorageEngine secured(StorageEngine storageEngine) {
        return SecureStorageEngine.secure(storageEngine);
    }
}

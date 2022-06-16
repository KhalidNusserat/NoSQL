package com.atypon.nosql.database.io;

import com.atypon.nosql.database.cache.LRUCache;

class CachedIOEngineTest extends IOEngineTest {

    @Override
    public IOEngine create() {
        return CachedIOEngine.from(
                new DefaultIOEngine(documentFactory),
                new LRUCache<>(100)
        );
    }
}
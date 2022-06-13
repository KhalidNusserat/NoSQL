package com.atypon.nosql.io;

import com.atypon.nosql.cache.LRUCache;
import com.google.gson.Gson;

class CachedIOEngineTest extends IOEngineTest {

    @Override
    public IOEngine create() {
        return CachedIOEngine.from(
                new DefaultIOEngine(),
                new LRUCache<>(100)
        );
    }
}
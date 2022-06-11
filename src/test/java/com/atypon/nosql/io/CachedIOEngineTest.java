package com.atypon.nosql.io;

import com.atypon.nosql.cache.LRUCache;
import com.google.gson.Gson;

class CachedIOEngineTest extends IOEngineTest {

    @Override
    public IOEngine create() {
        return CachedIOEngine.from(
                new GsonIOEngine(new Gson()),
                new LRUCache<>(100)
        );
    }
}
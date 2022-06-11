package com.atypon.nosql.io;

import com.google.gson.Gson;

class GsonIOEngineTest extends IOEngineTest {

    @Override
    public IOEngine create() {
        return new GsonIOEngine(new Gson());
    }
}
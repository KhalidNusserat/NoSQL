package com.atypon.nosql.database.io;

class BasicIOEngineTest extends IOEngineTest {

    @Override
    public IOEngine create() {
        return new BasicIOEngine(documentFactory);
    }
}
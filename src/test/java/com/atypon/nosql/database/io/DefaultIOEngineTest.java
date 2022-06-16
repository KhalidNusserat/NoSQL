package com.atypon.nosql.database.io;

class DefaultIOEngineTest extends IOEngineTest {

    @Override
    public IOEngine create() {
        return new DefaultIOEngine(documentFactory);
    }
}
package com.atypon.nosql.io;

class DefaultIOEngineTest extends IOEngineTest {

    @Override
    public IOEngine create() {
        return new DefaultIOEngine();
    }
}
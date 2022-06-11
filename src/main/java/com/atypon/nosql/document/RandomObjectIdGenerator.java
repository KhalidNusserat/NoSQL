package com.atypon.nosql.document;

import java.util.Random;

public class RandomObjectIdGenerator implements ObjectIdGenerator {
    private final Random random = new Random();

    @Override
    public String getNewId() {
        return Long.toString(random.nextLong());
    }
}

package com.atypon.nosql.database.document;

import java.util.Random;

public class RandomObjectIdGenerator implements ObjectIdGenerator {
    private final Random random = new Random();

    @Override
    public String getNewId() {
        return Long.toString(random.nextLong());
    }
}

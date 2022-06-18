package com.atypon.nosql.database.document;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RandomObjectIdGenerator implements ObjectIdGenerator {
    private final Random random = new Random();

    @Override
    public String getNewId() {
        return Long.toString(random.nextLong());
    }
}

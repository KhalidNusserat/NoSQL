package com.atypon.nosql.index;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class HashedFieldIndexJsonAdapterTest {
    private final Path testDirectory = Path.of("./test");

    @Test
    public void serializeAndDeserialize() {
        HashedFieldIndex<String, Double> index = new HashedFieldIndex<>();
        index.put("a", 1.0);
        index.put("b", 2.0);
        Gson gson = new Gson();
        String str = gson.toJson(index, new TypeToken<HashedFieldIndex<String, Double>>(){}.getType());
        HashedFieldIndex<String, Double> readIndex = gson.fromJson(
                str,
                new TypeToken<HashedFieldIndex<String, Double>>(){}.getType()
        );
        assertEquals(1, readIndex.getFromKey("a").orElseThrow());
        assertEquals(2, readIndex.getFromKey("b").orElseThrow());
    }
}
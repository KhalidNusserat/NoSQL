package com.atypon.nosql.index;

import com.atypon.nosql.utils.ReversedHashMap;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReversedHashMapJsonAdapterTest {
    private final Path testDirectory = Path.of("./test");

    @Test
    public void serializeAndDeserialize() {
        ReversedHashMap<String, Double> index = new ReversedHashMap<>();
        index.put("a", 2.0);
        index.put("b", 2.0);
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String str = gson.toJson(index, new TypeToken<ReversedHashMap<String, Double>>() {
        }.getType());
        ReversedHashMap<String, Double> readIndex = gson.fromJson(
                str,
                new TypeToken<ReversedHashMap<String, Double>>() {
                }.getType()
        );
        assertEquals(Set.of("a", "b"), readIndex.getFromValue(2.0));
    }
}
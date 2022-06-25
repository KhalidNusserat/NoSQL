package com.atypon.nosql.collection;

import java.nio.file.Path;

public record Stored<T>(T object, Path path) {
    public static <T> Stored<T> createStoredObject(T object, Path path) {
        return new Stored<>(object, path);
    }
}

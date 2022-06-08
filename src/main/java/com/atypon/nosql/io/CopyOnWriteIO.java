package com.atypon.nosql.io;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.Optional;

public interface CopyOnWriteIO {
    <T> Path write(T file, Type type, Path directory, String extension) throws IOException;

    <T> Optional<T> read(Path filepath, Type type);

    void delete(Path filepath);

    <T> Path update(T newFile, Type type, Path filepath, String extension) throws IOException;
}

package com.atypon.nosql.io;

import java.io.IOException;
import java.nio.file.Path;

public interface CopyOnWriteIO {
    <T> void write(T file, Path path) throws IOException;

    <T> T read(Path path, Class<T> tClass) throws IOException;

    void delete(Path path);

    <T> Path update(T newFile, Path path) throws IOException;
}

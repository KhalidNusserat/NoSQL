package com.atypon.nosql.io;

import java.io.IOException;
import java.nio.file.Path;

public interface CopyOnWriteIO {
    <T> Path write(T file, Path directory, String extension) throws IOException;

    <T> T read(Path filepath, Class<T> tClass) throws IOException;

    void delete(Path filepath);

    <T> Path update(T newFile, Path filepath, String extension) throws IOException;
}

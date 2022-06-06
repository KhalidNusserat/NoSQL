package com.atypon.nosql.index;

import java.io.IOException;
import java.nio.file.Path;

public interface FieldIndexIO {
    void write(FieldIndex<?, ?> fieldIndex, Path path) throws IOException;

    void delete(Path path);

    FieldIndex<?, ?> read(Path path) throws IOException;
}

package com.atypon.nosql.index;

import java.io.IOException;
import java.nio.file.Path;

public interface FieldIndexWriter {
    void write(FieldIndex<?, ?> fieldIndex, Path path) throws IOException;
}

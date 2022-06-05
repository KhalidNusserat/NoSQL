package com.atypon.nosql.index;

import java.io.IOException;
import java.nio.file.Path;

public interface FiledIndexReader {
    FieldIndex<?, ?> read(Path path) throws IOException;
}

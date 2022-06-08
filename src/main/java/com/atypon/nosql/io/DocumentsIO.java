package com.atypon.nosql.io;

import com.atypon.nosql.document.Document;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface DocumentsIO<T extends Document<?>> {
    Path write(T document, Path directory) throws IOException;

    Optional<T> read(Path documentPath);

    void delete(Path documentPath);

    Path update(T newDocument, Path documentPath) throws IOException;

    Collection<T> readAll(Path directoryPath);
}

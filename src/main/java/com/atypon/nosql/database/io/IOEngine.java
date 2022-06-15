package com.atypon.nosql.database.io;

import com.atypon.nosql.database.document.Document;
import com.atypon.nosql.database.document.DocumentGenerator;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public interface IOEngine {
    Path write(Document<?> document, Path directory) throws IOException;

    <T extends Document<?>> Optional<T> read(Path documentPath, DocumentGenerator<T> documentGenerator);

    void delete(Path documentPath);

    Path update(Document<?> updatedDocument, Path documentPath) throws IOException;

    <T extends Document<?>> List<T> readDirectory(Path directoryPath, DocumentGenerator<T> documentGenerator);
}
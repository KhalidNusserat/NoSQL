package com.atypon.nosql.database.io;

import com.atypon.nosql.database.document.Document;
import com.atypon.nosql.database.document.DocumentGenerator;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public interface IOEngine<T extends Document> {
    Path write(T document, Path directory);

    Optional<T> read(Path documentPath, DocumentGenerator<T> documentGenerator);

    void delete(Path documentPath);

    Path update(T updatedDocument, Path documentPath);

    List<T> readDirectory(Path directoryPath, DocumentGenerator<T> documentGenerator);
}

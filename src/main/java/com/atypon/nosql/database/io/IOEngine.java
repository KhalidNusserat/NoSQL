package com.atypon.nosql.database.io;

import com.atypon.nosql.database.collection.StoredDocument;
import com.atypon.nosql.database.document.Document;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public interface IOEngine {
    StoredDocument write(Document document, Path directory);

    Optional<Document> read(Path documentPath);

    void delete(Path documentPath);

    StoredDocument update(Document updatedDocument, Path documentPath);

    List<Document> readDirectory(Path directoryPath);
}

package com.atypon.nosql.storage;

import com.atypon.nosql.collection.StoredDocument;
import com.atypon.nosql.document.Document;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public interface StorageEngine {
    StoredDocument write(Document document, Path directory);

    Optional<Document> read(Path documentPath);

    void delete(Path documentPath);

    StoredDocument update(Document updatedDocument, Path documentPath);

    List<Document> readDirectory(Path directoryPath);
}

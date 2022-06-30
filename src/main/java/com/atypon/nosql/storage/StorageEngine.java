package com.atypon.nosql.storage;

import com.atypon.nosql.document.Document;
import com.atypon.nosql.utils.Stored;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public interface StorageEngine {
    Stored<Document> writeDocument(Document document, Path directory);

    Optional<Document> readDocument(Path documentPath);

    void deleteFile(Path documentPath);

    Stored<Document> updateDocument(Document updatedDocument, Path documentPath);

    List<Document> readDocumentsDirectory(Path directoryPath);
}

package com.atypon.nosql.database.index;

import com.atypon.nosql.database.document.Document;

import java.nio.file.Path;
import java.util.List;

public interface IndexesManager {
    void createIndex(Document indexFields);

    void removeIndex(Document indexFields);

    Index get(Document indexFields);

    List<Document> getAllIndexesFields();

    void addDocument(Document document, Path documentPath);

    void removeDocument(Document document);

    boolean contains(Document indexFields);

    void populateIndexes(Path documentsDirectory);
}

package com.atypon.nosql.index;

import com.atypon.nosql.document.Document;

import java.nio.file.Path;
import java.util.List;

public interface IndexesCollection {
    void createIndex(Document indexFields, boolean unique);

    void removeIndex(Document indexFields);

    Index get(Document indexFields);

    List<Document> getAllIndexesFields();

    void addDocument(Document document, Path documentPath);

    void removeDocument(Document document);

    boolean contains(Document indexFields);

    void populateIndexes();

    boolean checkUniqueConstraint(Document document);
}

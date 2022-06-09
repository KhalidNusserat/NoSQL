package com.atypon.nosql.index;

import com.atypon.nosql.document.Document;
import com.atypon.nosql.document.DocumentField;
import com.atypon.nosql.gsondocument.GsonDocument;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Set;

public interface FieldIndex<E, T extends Document<E>> {
    void initialize(Path directoryPath);

    void add(T document, Path documentPath);

    void remove(T document);

    Collection<Path> get(T matchDocument);

    Set<DocumentField> getDocumentFields();

    boolean contains(T matchDocument);
}

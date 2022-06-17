package com.atypon.nosql.database.index;

import com.atypon.nosql.database.document.Document;
import com.atypon.nosql.database.gsondocument.FieldsDoNotMatchException;

import java.nio.file.Path;
import java.util.Collection;

public interface Index {
    void add(Document document, Path documentPath);

    void remove(Document document);

    Collection<Path> get(Document matchDocument);

    boolean contains(Document matchDocument);

    Document getFields();
}

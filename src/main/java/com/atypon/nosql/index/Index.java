package com.atypon.nosql.index;

import com.atypon.nosql.document.Document;

import java.nio.file.Path;
import java.util.Collection;

public interface Index {
    void add(Document document, Path documentPath);

    void remove(Document document);

    Collection<Path> get(Document criteria);

    boolean contains(Document criteria);

    Document getFields();

    boolean checkUniqueConstraint(Document document);
}

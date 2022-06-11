package com.atypon.nosql.index;

import com.atypon.nosql.document.Document;
import com.atypon.nosql.gsondocument.FieldsDoNotMatchException;

import java.nio.file.Path;
import java.util.Collection;

public interface Index<T extends Document<?>> {
    void add(T document, Path documentPath) throws FieldsDoNotMatchException;

    void remove(T document) throws FieldsDoNotMatchException;

    Collection<Path> get(T matchDocument) throws FieldsDoNotMatchException;

    boolean contains(T matchDocument) throws FieldsDoNotMatchException;

    Path getIndexPath();

    void populateIndex(Path collectionPath);

    T getFields();
}
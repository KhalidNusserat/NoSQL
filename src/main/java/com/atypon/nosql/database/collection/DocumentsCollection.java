package com.atypon.nosql.database.collection;

import com.atypon.nosql.database.document.Document;
import com.atypon.nosql.database.gsondocument.FieldsDoNotMatchException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;

public interface DocumentsCollection<T extends Document<?>> {
    boolean contains(T matchDocument) throws FieldsDoNotMatchException;

    Collection<T> getAllThatMatches(T matchDocument) throws FieldsDoNotMatchException;

    Path addDocument(T document);

    Path updateDocument(T documentCriteria, T updatedDocument)
            throws NoSuchDocumentException, MultipleFilesMatchedException;

    int deleteAllThatMatches(T matchDocument) throws FieldsDoNotMatchException;

    Collection<T> getAll() throws IOException;
}

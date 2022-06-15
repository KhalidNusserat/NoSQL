package com.atypon.nosql.database.collection;

import com.atypon.nosql.database.document.Document;
import com.atypon.nosql.database.gsondocument.FieldsDoNotMatchException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;

public interface DocumentsCollection<T extends Document<?>> {
    boolean contains(T matchDocument) throws IOException, FieldsDoNotMatchException;

    Collection<T> getAllThatMatches(T matchDocument) throws IOException, FieldsDoNotMatchException;

    Path addDocument(T document) throws IOException;

    Path updateDocument(T documentCriteria, T updatedDocument)
            throws NoSuchDocumentException, MultipleFilesMatchedException, IOException;

    void deleteAllThatMatches(T matchDocument) throws IOException, FieldsDoNotMatchException;

    Collection<T> getAll() throws IOException;
}
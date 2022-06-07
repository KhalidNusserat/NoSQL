package com.atypon.nosql.collection;

import com.atypon.nosql.document.Document;
import com.atypon.nosql.document.DocumentParser;
import com.atypon.nosql.index.FieldIndex;
import com.atypon.nosql.schema.DocumentSchema;

import javax.naming.directory.SchemaViolationException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class IndexedDocumentsCollection<E, T extends Document<E>> implements DocumentsCollection<T> {
    private final DefaultDocumentsCollection<E, T> defaultDocumentsCollection;

    private final Map<String, Path> uniqueIndex = new ConcurrentHashMap<>();

    private final Map<Set<List<String>>, FieldIndex<String, List<E>>> indexes = new ConcurrentHashMap<>();

    public IndexedDocumentsCollection(DocumentSchema<T> documentSchema, DocumentParser<T> parser, Path directoryPath) {
        this.defaultDocumentsCollection = new DefaultDocumentsCollection<>(documentSchema, parser, directoryPath);
    }

    @Override
    public boolean contains(T bound) throws IOException {
        return false;
    }

    @Override
    public Collection<T> get(T bound) throws IOException {
        return null;
    }

    @Override
    public void put(T document) throws IOException, SchemaViolationException {

    }

    @Override
    public void remove(T bound) throws IOException {

    }

    @Override
    public Collection<T> readAll() throws IOException {
        return null;
    }
}

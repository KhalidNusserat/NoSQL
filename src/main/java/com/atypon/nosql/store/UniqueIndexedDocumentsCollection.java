package com.atypon.nosql.store;

import com.atypon.nosql.document.Document;
import com.atypon.nosql.document.ObjectID;
import com.atypon.nosql.index.FieldIndex;
import com.atypon.nosql.index.HashedFieldIndex;
import com.atypon.nosql.io.CopyOnWriteIO;
import com.atypon.nosql.io.GsonCopyOnWriteIO;
import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

public class UniqueIndexedDocumentsCollection<DocumentValue> implements DocumentsCollection<DocumentValue> {
    private final FieldIndex<ObjectID, Path> uniqueIndex;

    private final CopyOnWriteIO<Document<DocumentValue>> documentsIO = new GsonCopyOnWriteIO<>();

    private final Type documentType = new TypeToken<Document<DocumentValue>>(){}.getType();

    private final CopyOnWriteIO<FieldIndex<ObjectID, Path>> uniqueIndexIO = new GsonCopyOnWriteIO<>();

    private final Type uniqueIndexType = new TypeToken<FieldIndex<ObjectID, Path>>(){}.getType();

    private final Path path;

    private final PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**/*.index");

    private FieldIndex<ObjectID, Path> readUniqueIndex() throws IOException {
        try (Stream<Path> paths = Files.walk(path)) {
            Optional<Path> uniqueIndexPath = paths.filter(matcher::matches).findAny();
            if (uniqueIndexPath.isPresent()) {
                return uniqueIndexIO.read(uniqueIndexPath.get(), uniqueIndexType);
            } else {
                return new HashedFieldIndex<>();
            }
        }
    }

    public UniqueIndexedDocumentsCollection(Path path) throws IOException {
        Preconditions.checkNotNull(path);
        this.uniqueIndex = readUniqueIndex();
        this.path = path;
    }

    @Override
    public boolean containsID(ObjectID id) {
        Preconditions.checkNotNull(id);
        return uniqueIndex.containsKey(id);
    }

    @Override
    public Document<DocumentValue> get(ObjectID id) throws IOException {
        Preconditions.checkNotNull(id);
        Preconditions.checkState(uniqueIndex.containsKey(id), ItemNotFoundException.class);
        return documentsIO.read(uniqueIndex.getFromKey(id).orElseThrow(), documentType);
    }

    @Override
    public void put(ObjectID id, Document<DocumentValue> document) throws IOException {
        Preconditions.checkNotNull(id, document);
        Preconditions.checkState(uniqueIndex.containsKey(id), ItemNotFoundException.class);
        uniqueIndex.put(id, documentsIO.update(document, uniqueIndex.getFromKey(id).orElseThrow(), ".json"));
        uniqueIndexIO.update(uniqueIndex, path, ".index");
    }

    @Override
    public void remove(ObjectID id) throws IOException {
        Preconditions.checkNotNull(id);
        Preconditions.checkState(uniqueIndex.containsKey(id), ItemNotFoundException.class);
        documentsIO.delete(uniqueIndex.getFromKey(id).orElseThrow());
        uniqueIndex.remove(id);
        uniqueIndexIO.update(uniqueIndex, path, ".index");
    }

    @Override
    public Collection<Document<DocumentValue>> readAll() throws IOException {
        return Files.walk(path)
                .filter(filepath -> !matcher.matches(filepath))
                .map(filepath -> {
                    try {
                        return documentsIO.read(filepath, documentType);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();
    }
}
package com.atypon.nosql.collection;

import com.atypon.nosql.document.Document;
import com.atypon.nosql.document.DocumentParser;
import com.atypon.nosql.io.CopyOnWriteIO;
import com.atypon.nosql.io.GsonCopyOnWriteIO;
import com.atypon.nosql.schema.DocumentSchema;

import javax.naming.directory.SchemaViolationException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

public class DefaultDocumentsCollection<E, T extends Document<E>> implements DocumentsCollection<T> {
    private final DocumentSchema<T> documentSchema;

    private final CopyOnWriteIO io = new GsonCopyOnWriteIO();

    private final Path directoryPath;

    private final DocumentMatcher<E, T> documentMatcher;

    public DefaultDocumentsCollection(DocumentSchema<T> documentSchema, DocumentParser<T> parser, Path directoryPath) {
        this.documentSchema = documentSchema;
        this.directoryPath = directoryPath;
        documentMatcher = new DocumentMatcher<>(directoryPath, parser, io);
    }


    @Override
    public boolean contains(T matchDocument) throws IOException {
        return documentMatcher.contains(matchDocument);
    }

    @Override
    public Collection<T> get(T matchDocument) throws IOException {
        return documentMatcher.getAll(matchDocument);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void put(T document) throws IOException, SchemaViolationException {
        T validatedDocument = documentSchema.validate(document);
        List<Path> paths = documentMatcher.getPaths((T) document.matchID());
        if (paths.size() == 1) {
            io.update(validatedDocument.toString(), String.class, paths.get(0), ".json");
        } else if (paths.size() == 0) {
            io.write(validatedDocument.toString(), String.class, directoryPath, ".json");
        } else {
            throw new IllegalStateException("There are multiple files with the same ObjectID");
        }
    }

    @Override
    public void remove(T matchDocument) throws IOException {
        for (Path path : documentMatcher.getPaths(matchDocument)) {
            io.delete(path);
        }
    }

    @Override
    public Collection<T> getAll() throws IOException {
        return documentMatcher.getAll();
    }
}
package com.atypon.nosql.collection;

import com.atypon.nosql.document.Document;
import com.atypon.nosql.document.DocumentParser;
import com.atypon.nosql.io.CopyOnWriteIO;
import com.atypon.nosql.schema.DocumentSchema;
import com.google.common.base.Preconditions;

import javax.naming.directory.SchemaViolationException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

public class DefaultDocumentsCollection<E, T extends Document<E>> implements DocumentsCollection<T> {
    private final CopyOnWriteIO io;

    private final Path directoryPath;

    private final DocumentUtils<E, T> documentUtils;

    private DefaultDocumentsCollection(
            CopyOnWriteIO io,
            DocumentParser<T> parser,
            Path directoryPath
    ) {
        this.io = io;
        this.directoryPath = directoryPath;
        documentUtils = new DocumentUtils<>(directoryPath, parser, this.io);
    }

    public static <E, T extends Document<E>> DefaultDocumentsCollectionBuilder<E, T> builder() {
        return new DefaultDocumentsCollectionBuilder<>();
    }

    @Override
    public boolean contains(T matchDocument) throws IOException {
        return documentUtils.contains(matchDocument);
    }

    @Override
    public Collection<T> getAllThatMatches(T matchDocument) throws IOException {
        return documentUtils.getAllThatMatches(matchDocument);
    }

    @Override
    public Collection<T> getAll() throws IOException {
        return documentUtils.getAll();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Path put(T document) throws IOException, SchemaViolationException {
        List<Path> paths = documentUtils.getPaths((T) document.matchID());
        if (paths.size() == 1) {
            return io.update(document.toString(), String.class, paths.get(0), ".json");
        } else if (paths.size() == 0) {
            return io.write(document.toString(), String.class, directoryPath, ".json");
        } else {
            throw new IllegalStateException("There are multiple files with the same ObjectID");
        }
    }

    @Override
    public void remove(T matchDocument) throws IOException {
        for (Path path : documentUtils.getPaths(matchDocument)) {
            io.delete(path);
        }
    }

    public static class DefaultDocumentsCollectionBuilder<E, T extends Document<E>> {
        private CopyOnWriteIO io;

        private Path directoryPath;

        private DocumentParser<T> documentParser;

        public DefaultDocumentsCollectionBuilder<E, T> setIO(CopyOnWriteIO io) {
            this.io = io;
            return this;
        }

        public DefaultDocumentsCollectionBuilder<E, T> setDirectoryPath(Path directoryPath) {
            this.directoryPath = directoryPath;
            return this;
        }

        public DefaultDocumentsCollectionBuilder<E, T> setDocumentParser(DocumentParser<T> documentParser) {
            this.documentParser = documentParser;
            return this;
        }

        public DefaultDocumentsCollection<E, T> create() {
            Preconditions.checkNotNull(io);
            Preconditions.checkNotNull(directoryPath);
            Preconditions.checkNotNull(documentParser);
            return new DefaultDocumentsCollection<>(io, documentParser, directoryPath);
        }
    }
}
package com.atypon.nosql.collection;

import com.atypon.nosql.document.Document;
import com.atypon.nosql.io.DocumentsIO;
import com.google.common.base.Preconditions;

import javax.naming.directory.SchemaViolationException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

public class DefaultDocumentsCollection<E, T extends Document<E>> implements DocumentsCollection<T> {
    private final DocumentsIO<T> documentsIO;

    private final Path directoryPath;

    private final DocumentsMatchIO<E, T> documentsMatchIO;

    private DefaultDocumentsCollection(
            DocumentsIO<T> documentsIO,
            Path directoryPath,
            DocumentsMatchIO<E, T> documentsMatchIO
    ) {
        this.documentsIO = documentsIO;
        this.directoryPath = directoryPath;
        this.documentsMatchIO = documentsMatchIO;
    }

    public static <E, T extends Document<E>> DefaultDocumentsCollectionBuilder<E, T> builder() {
        return new DefaultDocumentsCollectionBuilder<>();
    }

    @Override
    public boolean contains(T matchDocument) throws IOException {
        return documentsMatchIO.contains(matchDocument, directoryPath);
    }

    @Override
    public Collection<T> getAllThatMatches(T matchDocument) throws IOException {
        return documentsMatchIO.getAllThatMatches(matchDocument, directoryPath);
    }

    @Override
    public Collection<T> getAll() {
        return documentsIO.readAll(directoryPath);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Path put(T document) throws IOException, SchemaViolationException {
        List<Path> paths = documentsMatchIO.getPaths((T) document.matchID(), directoryPath);
        if (paths.size() == 1) {
            return documentsIO.update(document, paths.get(0));
        } else if (paths.size() == 0) {
            return documentsIO.write(document, directoryPath);
        } else {
            throw new IllegalStateException("There are multiple files with the same ObjectID");
        }
    }

    @Override
    public void remove(T matchDocument) throws IOException {
        for (Path path : documentsMatchIO.getPaths(matchDocument, directoryPath)) {
            documentsIO.delete(path);
        }
    }

    public static class DefaultDocumentsCollectionBuilder<E, T extends Document<E>> {
        private DocumentsIO<T> io;

        private Path directoryPath;

        private DocumentsMatchIO<E, T> documentsMatchIO;

        public DefaultDocumentsCollectionBuilder<E, T> setDocumentsIO(DocumentsIO<T> io) {
            this.io = io;
            return this;
        }

        public DefaultDocumentsCollectionBuilder<E, T> setDirectoryPath(Path directoryPath) {
            this.directoryPath = directoryPath;
            return this;
        }

        public DefaultDocumentsCollectionBuilder<E, T> setDocumentsMatchIO(DocumentsMatchIO<E, T> documentsMatchIO) {
            this.documentsMatchIO = documentsMatchIO;
            return this;
        }

        public DefaultDocumentsCollection<E, T> create() {
            Preconditions.checkNotNull(io);
            Preconditions.checkNotNull(directoryPath);
            Preconditions.checkNotNull(documentsMatchIO);
            return new DefaultDocumentsCollection<>(io, directoryPath, documentsMatchIO);
        }
    }
}
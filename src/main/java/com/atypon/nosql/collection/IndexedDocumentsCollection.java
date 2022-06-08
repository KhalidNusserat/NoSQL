package com.atypon.nosql.collection;

import com.atypon.nosql.document.Document;
import com.atypon.nosql.document.DocumentField;
import com.atypon.nosql.document.DocumentParser;
import com.atypon.nosql.index.FieldIndex;
import com.atypon.nosql.index.HashedFieldIndex;
import com.atypon.nosql.io.CopyOnWriteIO;
import com.atypon.nosql.schema.DocumentSchema;
import com.google.common.base.Preconditions;

import javax.naming.directory.SchemaViolationException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class IndexedDocumentsCollection<E, T extends Document<E>> implements DocumentsCollection<T> {
    private final DefaultDocumentsCollection<E, T> defaultDocumentsCollection;

    private final CopyOnWriteIO io;

    private final DocumentUtils<E, T> documentUtils;

    private final Map<String, Path> uniqueIndex = new ConcurrentHashMap<>();

    private final Map<Set<DocumentField>, FieldIndex<String, Set<E>>> indexes = new ConcurrentHashMap<>();

    private IndexedDocumentsCollection(
            CopyOnWriteIO io,
            DocumentParser<T> documentParser,
            Path directoryPath
    ) {
        this.defaultDocumentsCollection = DefaultDocumentsCollection.<E, T>builder()
                .setDirectoryPath(directoryPath)
                .setDocumentParser(documentParser)
                .setIO(io)
                .create();
    this.io = io;
        documentUtils = new DocumentUtils<>(directoryPath, documentParser, io);
    }

    @Override
    public boolean contains(T matchDocument) throws IOException {
        Set<DocumentField> fields = matchDocument.getFields();
        if (indexes.containsKey(fields)) {
            return indexes.get(fields).containsValue(matchDocument.getAll());
        } else {
            return defaultDocumentsCollection.contains(matchDocument);
        }
    }

    @Override
    public Collection<T> getAllThatMatches(T matchDocument) throws IOException {
        Set<DocumentField> fields = matchDocument.getFields();
        if (indexes.containsKey(fields)) {
            return indexes.get(fields)
                    .getFromValue(matchDocument.getAll(fields))
                    .stream()
                    .map(uniqueIndex::get)
                    .map(documentUtils::readDocument)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
        } else {
            return defaultDocumentsCollection.getAllThatMatches(matchDocument);
        }
    }

    @Override
    public Path put(T document) throws IOException, SchemaViolationException {
        for (Set<DocumentField> fields : indexes.keySet()) {
            indexes.putIfAbsent(fields, new HashedFieldIndex<>());
            indexes.get(fields).put(document.id(), document.getAll(fields));
        }
        Path documentPath = defaultDocumentsCollection.put(document);
        uniqueIndex.put(document.id(), documentPath);
        return documentPath;
    }

    @Override
    public void remove(T matchDocument) throws IOException {
        Set<DocumentField> fields = matchDocument.getFields();
        if (indexes.containsKey(fields)) {
            indexes.get(fields).getFromValue(matchDocument.getAll(fields))
                    .stream()
                    .map(uniqueIndex::get)
                    .forEach(io::delete);
        } else {
            defaultDocumentsCollection.remove(matchDocument);
        }
    }

    @Override
    public Collection<T> getAll() throws IOException {
        return defaultDocumentsCollection.getAll();
    }

    public static class IndexedDocumentsCollectionsBuilder<E, T extends Document<E>> {
        private CopyOnWriteIO io;

        private Path directoryPath;

        private DocumentParser<T> documentParser;

        public IndexedDocumentsCollectionsBuilder<E, T> setIO(CopyOnWriteIO io) {
            this.io = io;
            return this;
        }

        public IndexedDocumentsCollectionsBuilder<E, T> setDirectoryPath(Path directoryPath) {
            this.directoryPath = directoryPath;
            return this;
        }

        public IndexedDocumentsCollectionsBuilder<E, T> setDocumentParser(DocumentParser<T> documentParser) {
            this.documentParser = documentParser;
            return this;
        }

        public IndexedDocumentsCollection<E, T> create() {
            Preconditions.checkNotNull(io);
            Preconditions.checkNotNull(directoryPath);
            Preconditions.checkNotNull(documentParser);
            return new IndexedDocumentsCollection<>(io, documentParser, directoryPath);
        }
    }
}

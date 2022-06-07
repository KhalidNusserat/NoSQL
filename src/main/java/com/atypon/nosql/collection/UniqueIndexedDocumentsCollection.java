package com.atypon.nosql.collection;

import com.atypon.nosql.document.Document;
import com.atypon.nosql.document.DocumentParser;
import com.atypon.nosql.document.ObjectID;
import com.atypon.nosql.index.FieldIndex;
import com.atypon.nosql.index.HashedFieldIndex;
import com.atypon.nosql.io.CopyOnWriteIO;
import com.atypon.nosql.io.GsonCopyOnWriteIO;
import com.atypon.nosql.schema.DocumentSchema;
import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;

import javax.naming.directory.SchemaViolationException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

public class UniqueIndexedDocumentsCollection<T extends Document<?>> implements DocumentsCollection<T> {
    private final FieldIndex<ObjectID, String> uniqueIndex;

    private final DocumentSchema<T> documentSchema;

    private final CopyOnWriteIO io = new GsonCopyOnWriteIO();

    private final DocumentParser<T> parser;

    private final Type uniqueIndexType = new TypeToken<HashedFieldIndex<ObjectID, String>>() {
    }.getType();

    private final Path directoryPath;

    private final PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**/*.index");

    public UniqueIndexedDocumentsCollection(
            DocumentSchema<T> documentSchema,
            DocumentParser<T> parser,
            Path directoryPath) throws IOException
    {
        Preconditions.checkNotNull(documentSchema);
        Preconditions.checkNotNull(directoryPath);
        Preconditions.checkNotNull(parser);
        this.directoryPath = directoryPath;
        this.documentSchema = documentSchema;
        this.parser = parser;
        this.uniqueIndex = readUniqueIndex();
    }

    private Optional<Path> getUniqueIndexPath() throws IOException {
        try (Stream<Path> paths = Files.walk(directoryPath)) {
            return paths.filter(matcher::matches).findAny();
        }
    }

    private FieldIndex<ObjectID, String> readUniqueIndex() throws IOException {
        Optional<Path> uniqueIndexPath = getUniqueIndexPath();
        if (uniqueIndexPath.isPresent()) {
            return io.read(uniqueIndexPath.get(), uniqueIndexType);
        } else {
            io.write(new HashedFieldIndex<>(), uniqueIndexType, directoryPath, ".index");
            return new HashedFieldIndex<>();
        }
    }

    @Override
    public boolean containsID(ObjectID id) {
        Preconditions.checkNotNull(id);
        return uniqueIndex.containsKey(id);
    }

    @Override
    public T get(ObjectID id) throws IOException {
        Preconditions.checkNotNull(id);
        Preconditions.checkState(uniqueIndex.containsKey(id), ItemNotFoundException.class);
        return parser.parse(
                io.read(
                        Path.of(uniqueIndex.getFromKey(id).orElseThrow()),
                        String.class
                )
        );
    }

    @Override
    public void put(ObjectID id, T document) throws IOException, SchemaViolationException {
        Preconditions.checkNotNull(id, document);
        T validatedDocument = documentSchema.validate(document);
        if (uniqueIndex.containsKey(id)) {
            uniqueIndex.put(
                    id,
                    io.update(
                            validatedDocument.toString(),
                            String.class,
                            Path.of(uniqueIndex.getFromKey(id).orElseThrow()),
                            ".json"
                    ).toString()
            );
        } else {
            uniqueIndex.put(
                    id,
                    io.write(
                            validatedDocument.toString(),
                            String.class,
                            directoryPath,
                            ".json"
                    ).toString()
            );
        }
        io.update(uniqueIndex, uniqueIndexType, getUniqueIndexPath().orElseThrow(), ".index");
    }

    @Override
    public void remove(ObjectID id) throws IOException {
        Preconditions.checkNotNull(id);
        Preconditions.checkState(uniqueIndex.containsKey(id), ItemNotFoundException.class);
        io.delete(Path.of(uniqueIndex.getFromKey(id).orElseThrow()));
        uniqueIndex.remove(id);
        io.update(uniqueIndex, uniqueIndexType, getUniqueIndexPath().orElseThrow(), ".index");
    }

    @Override
    public Collection<T> readAll() throws IOException {
        return Files.walk(directoryPath)
                .filter(filepath -> Files.isRegularFile(filepath) && !matcher.matches(filepath))
                .map(filepath -> {
                    try {
                        return parser.parse(io.read(filepath, String.class));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();
    }
}
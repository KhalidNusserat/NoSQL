package com.atypon.nosql.collection;

import com.atypon.nosql.document.Document;
import com.atypon.nosql.document.DocumentParser;
import com.atypon.nosql.io.CopyOnWriteIO;
import com.atypon.nosql.io.GsonCopyOnWriteIO;
import com.atypon.nosql.schema.DocumentSchema;
import com.atypon.nosql.utils.ExtraFileUtils;

import javax.naming.directory.SchemaViolationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

public class DefaultDocumentsCollection<E, T extends Document<E>> implements DocumentsCollection<T> {
    private final DocumentSchema<T> documentSchema;

    private final CopyOnWriteIO io = new GsonCopyOnWriteIO();

    private final DocumentParser<T> parser;

    private final Path directoryPath;

    public DefaultDocumentsCollection(DocumentSchema<T> documentSchema, DocumentParser<T> parser, Path directoryPath) {
        this.documentSchema = documentSchema;
        this.parser = parser;
        this.directoryPath = directoryPath;
    }

    private List<Path> getPaths(T bound) throws IOException {
        return Files.walk(directoryPath)
                .filter(ExtraFileUtils::isJsonFile)
                .filter(path -> {
                    try {
                        T parsedDocument = parser.parse(io.read(path, String.class));
                        return parsedDocument.matches(bound);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();
    }


    @Override
    public boolean contains(T bound) throws IOException {
        return !get(bound).isEmpty();
    }

    @Override
    public Collection<T> get(T bound) throws IOException {
        return readAll().stream().filter(document -> document.matches(bound)).toList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void put(T document) throws IOException, SchemaViolationException {
        T validatedDocument = documentSchema.validate(document);
        List<Path> paths = getPaths((T) document.matchID());
        if (paths.size() == 1) {
            io.update(validatedDocument.toString(), String.class, paths.get(0), ".json");
        } else if (paths.size() == 0) {
            io.write(validatedDocument.toString(), String.class, directoryPath, ".json");
        } else {
            throw new IllegalStateException("There are multiple files with the same ObjectID");
        }
    }

    @Override
    public void remove(T bound) throws IOException {
        for (Path path : getPaths(bound)) {
            io.delete(path);
        }
    }

    @Override
    public Collection<T> readAll() throws IOException {
        return Files.walk(directoryPath)
                .filter(ExtraFileUtils::isJsonFile)
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
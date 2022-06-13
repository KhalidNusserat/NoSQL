package com.atypon.nosql.index;

import com.atypon.nosql.document.Document;
import com.atypon.nosql.document.DocumentGenerator;
import com.atypon.nosql.gsondocument.FieldsDoNotMatchException;
import com.atypon.nosql.io.IOEngine;
import com.atypon.nosql.utils.ExtraFileUtils;
import com.atypon.nosql.utils.ReversedHashMap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DefaultIndex<T extends Document<?>> implements Index<T> {
    private final T fieldsDocument;

    private final ReversedHashMap<Path, T> pathToValues;

    private final Path indexPath;

    private final ExecutorService indexPopulatingService = Executors.newCachedThreadPool();

    private final IOEngine ioEngine;

    private final DocumentGenerator<T> documentGenerator;

    public DefaultIndex(T fieldsDocument, Path indexPath, IOEngine ioEngine, DocumentGenerator<T> documentGenerator) {
        this.fieldsDocument = fieldsDocument;
        this.indexPath = indexPath;
        this.ioEngine = ioEngine;
        this.documentGenerator = documentGenerator;
        pathToValues = new ReversedHashMap<>();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void add(T document, Path documentPath) throws FieldsDoNotMatchException {
        pathToValues.put(documentPath, (T) document.getValuesToMatch(fieldsDocument));
    }

    @Override
    @SuppressWarnings("unchecked")
    public void remove(T document) throws FieldsDoNotMatchException {
        pathToValues.removeByValue((T) document.getValuesToMatch(fieldsDocument));
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<Path> get(T document) throws FieldsDoNotMatchException {
        return pathToValues.getFromValue((T) document.getValuesToMatch(fieldsDocument));
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean contains(T document) throws FieldsDoNotMatchException {
        return pathToValues.containsValue((T) document.getValuesToMatch(fieldsDocument));
    }

    @Override
    public Path getIndexPath() {
        return indexPath;
    }

    @Override
    public void populateIndex(Path collectionPath) {
        try {
            List<Path> paths = Files.walk(collectionPath, 1)
                    .filter(ExtraFileUtils::isJsonFile)
                    .toList();
            for (Path path : paths) {
                Optional<T> document = ioEngine.read(path, documentGenerator);
                if (document.isPresent()) {
                    try {
                        add(document.get(), path);
                    } catch (FieldsDoNotMatchException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public T getFields() {
        return fieldsDocument;
    }
}

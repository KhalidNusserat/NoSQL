package com.atypon.nosql.database.index;

import com.atypon.nosql.database.document.Document;
import com.atypon.nosql.database.io.IOEngine;
import com.atypon.nosql.database.utils.FileUtils;
import com.atypon.nosql.database.utils.ReversedHashMap;

import java.nio.file.Path;
import java.util.Collection;

public class DefaultIndex implements Index {
    private final Document fieldsDocument;

    private final ReversedHashMap<Path, Document> pathToValues;

    private final Path indexPath;

    private final IOEngine ioEngine;

    public DefaultIndex(
            Document fieldsDocument,
            Path indexPath,
            IOEngine ioEngine) {
        this.fieldsDocument = fieldsDocument;
        this.indexPath = indexPath;
        this.ioEngine = ioEngine;
        pathToValues = new ReversedHashMap<>();
    }

    @Override
    public void add(Document document, Path documentPath) {
        pathToValues.put(documentPath, document.getValuesToMatch(fieldsDocument));
    }

    @Override
    public void remove(Document document) {
        pathToValues.removeByValue(document.getValuesToMatch(fieldsDocument));
    }

    @Override
    public Collection<Path> get(Document document) {
        return pathToValues.getFromValue(document.getValuesToMatch(fieldsDocument));
    }

    @Override
    public boolean contains(Document document) {
        return pathToValues.containsValue(document.getValuesToMatch(fieldsDocument));
    }

    @Override
    public Path getIndexPath() {
        return indexPath;
    }

    @Override
    public void populateIndex(Path collectionPath) {
        FileUtils.traverseDirectory(collectionPath)
                .filter(FileUtils::isJsonFile)
                .forEach(this::addDocumentToIndex);
    }

    private void addDocumentToIndex(Path documentPath) {
        ioEngine.read(documentPath).ifPresent(value -> add(value, documentPath));
    }

    @Override
    public Document getFields() {
        return fieldsDocument;
    }
}

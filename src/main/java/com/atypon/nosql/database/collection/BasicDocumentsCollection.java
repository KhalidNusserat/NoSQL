package com.atypon.nosql.database.collection;

import com.atypon.nosql.database.document.Document;
import com.atypon.nosql.database.io.IOEngine;
import com.atypon.nosql.database.utils.FileUtils;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class BasicDocumentsCollection implements DocumentsCollection {
    private final IOEngine ioEngine;

    private final Path documentsPath;

    public BasicDocumentsCollection(Path collectionPath, IOEngine ioEngine) {
        this.ioEngine = ioEngine;
        documentsPath = collectionPath;
        FileUtils.createDirectories(documentsPath);
    }

    @Override
    public boolean contains(Document documentCriteria) {
        return FileUtils.traverseDirectory(documentsPath)
                .filter(FileUtils::isJsonFile)
                .map(ioEngine::read)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .anyMatch(documentCriteria::subsetOf);
    }

    @Override
    public List<Document> getAllThatMatch(Document documentCriteria) {
        return FileUtils.traverseDirectory(documentsPath)
                .filter(FileUtils::isJsonFile)
                .map(ioEngine::read)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(documentCriteria::subsetOf)
                .toList();
    }

    @Override
    public Path addDocument(Document addedDocument) {
        return ioEngine.write(addedDocument, documentsPath);
    }

    @Override
    public Path updateDocument(Document documentCriteria, Document updatedDocument) {
        List<Path> matchingDocumentsPaths = getPathsThatMatch(documentCriteria);
        if (matchingDocumentsPaths.size() > 1) {
            throw new MultipleFilesMatchedException(matchingDocumentsPaths.size());
        } else if (matchingDocumentsPaths.size() == 0) {
            throw new NoSuchDocumentException(documentCriteria);
        } else {
            return ioEngine.update(updatedDocument, matchingDocumentsPaths.get(0));
        }
    }

    private List<Path> getPathsThatMatch(Document documentCriteria) {
        return FileUtils.traverseDirectory(documentsPath)
                .filter(FileUtils::isJsonFile)
                .filter(path -> ioEngine.read(path).map(documentCriteria::subsetOf).orElseThrow())
                .toList();
    }

    @Override
    public int removeAllThatMatch(Document documentCriteria) {
        List<Path> paths = getPathsThatMatch(documentCriteria);
        paths.forEach(ioEngine::delete);
        return paths.size();
    }

    @Override
    public List<Document> getAll() {
        return FileUtils.traverseDirectory(documentsPath)
                .filter(FileUtils::isJsonFile)
                .map(ioEngine::read)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }
}
package com.atypon.nosql.database.collection;

import com.atypon.nosql.database.document.Document;
import com.atypon.nosql.database.io.IOEngine;
import com.atypon.nosql.database.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Slf4j
public class DefaultBasicDocumentsCollection implements DocumentsCollection {
    private final IOEngine ioEngine;

    private final Path documentsPath;

    public DefaultBasicDocumentsCollection(Path collectionPath, IOEngine ioEngine) {
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
            log.error(
                    "More than one document matched: \"{}\" matched [{}] documents, expected [1]",
                    documentCriteria,
                    matchingDocumentsPaths.size()
            );
            throw new MultipleFilesMatchedException(matchingDocumentsPaths.size());
        } else if (matchingDocumentsPaths.size() == 0) {
            log.error(
                    "No documents matched: \"{}\" matched [0] documents, expected [1]",
                    documentCriteria
            );
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
package com.atypon.nosql.collection;

import com.atypon.nosql.document.Document;
import com.atypon.nosql.storage.StorageEngine;
import com.atypon.nosql.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Slf4j
public class DefaultBasicDocumentsCollection implements DocumentsCollection {
    private final StorageEngine storageEngine;

    private final Path documentsPath;

    public DefaultBasicDocumentsCollection(Path collectionPath, StorageEngine storageEngine) {
        this.storageEngine = storageEngine;
        documentsPath = collectionPath;
        FileUtils.createDirectories(documentsPath);
    }

    @Override
    public boolean contains(Document documentCriteria) {
        return FileUtils.traverseDirectory(documentsPath)
                .filter(FileUtils::isJsonFile)
                .map(storageEngine::readDocument)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .anyMatch(documentCriteria::subsetOf);
    }

    @Override
    public List<Document> getAllThatMatch(Document documentCriteria) {
        return FileUtils.traverseDirectory(documentsPath)
                .filter(FileUtils::isJsonFile)
                .map(storageEngine::readDocument)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(documentCriteria::subsetOf)
                .toList();
    }

    @Override
    public List<Stored<Document>> addDocuments(List<Document> documents) {
        return documents.stream().map(
                document -> storageEngine.writeDocument(document, documentsPath)
        ).toList();
    }

    @Override
    public List<Stored<Document>> updateDocuments(Document documentCriteria, Document updatedDocument) {
        List<Path> matchingDocumentsPaths = getPathsThatMatch(documentCriteria);
        return matchingDocumentsPaths.stream()
                .map(documentPath -> storageEngine.updateDocument(updatedDocument, documentPath))
                .toList();
    }

    private List<Path> getPathsThatMatch(Document documentCriteria) {
        return FileUtils.traverseDirectory(documentsPath)
                .filter(FileUtils::isJsonFile)
                .filter(path -> storageEngine.readDocument(path).map(documentCriteria::subsetOf).orElseThrow())
                .toList();
    }

    @Override
    public int removeAllThatMatch(Document documentCriteria) {
        List<Path> paths = getPathsThatMatch(documentCriteria);
        paths.forEach(storageEngine::deleteFile);
        return paths.size();
    }

    @Override
    public List<Document> getAll() {
        return FileUtils.traverseDirectory(documentsPath)
                .filter(FileUtils::isJsonFile)
                .map(storageEngine::readDocument)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }
}
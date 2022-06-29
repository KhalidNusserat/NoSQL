package com.atypon.nosql.collection;

import com.atypon.nosql.document.Document;
import com.atypon.nosql.storage.StorageEngine;
import com.atypon.nosql.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
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
    public Optional<Document> findFirst(Document criteria) {
        return findAll(criteria).stream().findFirst();
    }

    @Override
    public List<Document> findAll(Document criteria) {
        return FileUtils.traverseDirectory(documentsPath)
                .filter(FileUtils::isJsonFile)
                .map(storageEngine::readDocument)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(criteria::subsetOf)
                .toList();
    }

    @Override
    public List<Stored<Document>> addAll(List<Document> documents) {
        return documents.stream().map(
                document -> storageEngine.writeDocument(document, documentsPath)
        ).toList();
    }

    @Override
    public List<Stored<Document>> updateAll(Document criteria, Document update) {
        List<Path> matchingDocumentsPaths = getPathsThatMatch(criteria);
        return matchingDocumentsPaths.stream()
                .map(documentPath -> updateDocument(documentPath, update))
                .filter(Objects::nonNull)
                .toList();
    }

    private Stored<Document> updateDocument(Path documentPath, Document update) {
        Optional<Document> optionalDocument = storageEngine.readDocument(documentPath);
        if (optionalDocument.isPresent()) {
            Document document = optionalDocument.get();
            Document updatedDocument = document.overrideFields(update);
            return storageEngine.updateDocument(updatedDocument, documentPath);
        } else {
            return null;
        }
    }

    private List<Path> getPathsThatMatch(Document documentCriteria) {
        return FileUtils.traverseDirectory(documentsPath)
                .filter(FileUtils::isJsonFile)
                .filter(path -> storageEngine.readDocument(path).map(documentCriteria::subsetOf).orElseThrow())
                .toList();
    }

    @Override
    public int removeAll(Document documentCriteria) {
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
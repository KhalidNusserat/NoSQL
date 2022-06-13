package com.atypon.nosql.collection;

import com.atypon.nosql.document.Document;
import com.atypon.nosql.document.DocumentGenerator;
import com.atypon.nosql.io.IOEngine;
import com.atypon.nosql.utils.ExtraFileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class GenericDefaultDocumentsCollection<T extends Document<?>> implements DocumentsCollection<T> {
    private final IOEngine ioEngine;

    private final Path documentsPath;

    private final DocumentGenerator<T> documentGenerator;

    public GenericDefaultDocumentsCollection(
            IOEngine ioEngine,
            Path collectionPath,
            DocumentGenerator<T> documentGenerator
    ) {
        this.ioEngine = ioEngine;
        this.documentGenerator = documentGenerator;
        documentsPath = collectionPath.resolve("documents/");
        try {
            Files.createDirectories(documentsPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean contains(T documentCriteria) {
        try {
            return Files.walk(documentsPath, 1)
                    .filter(ExtraFileUtils::isJsonFile)
                    .map(documentPath -> ioEngine.read(documentPath, documentGenerator))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .anyMatch(documentCriteria::subsetOf);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<T> getAllThatMatches(T documentCriteria) {
        try {
            return Files.walk(documentsPath, 1)
                    .filter(ExtraFileUtils::isJsonFile)
                    .map(documentPath -> ioEngine.read(documentPath, documentGenerator))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .filter(documentCriteria::subsetOf)
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<T> getAll() {
        return ioEngine.readDirectory(documentsPath, documentGenerator);
    }

    @Override
    public Path addDocument(T addedDocument) throws IOException {
        return ioEngine.write(addedDocument, documentsPath);
    }

    private List<Path> getPathsThatMatch(T documentCriteria) {
        try {
            return Files.walk(documentsPath, 1)
                    .filter(ExtraFileUtils::isJsonFile)
                    .filter(path -> ioEngine.read(path, documentGenerator)
                            .map(documentCriteria::subsetOf)
                            .orElse(false))
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Path updateDocument(T oldDocument, T updatedDocument)
            throws NoSuchDocumentException, MultipleFilesMatchedException, IOException
    {
        List<Path> matchingDocumentsPaths = getPathsThatMatch(oldDocument);
        if (matchingDocumentsPaths.size() > 1) {
            throw new MultipleFilesMatchedException(matchingDocumentsPaths.size());
        } else if (matchingDocumentsPaths.size() == 0) {
            throw new NoSuchDocumentException(oldDocument);
        } else {
            return ioEngine.update(oldDocument, matchingDocumentsPaths.get(0));
        }
    }

    @Override
    public void deleteAllThatMatches(T documentCriteria) {
        getPathsThatMatch(documentCriteria).forEach(ioEngine::delete);
    }
}
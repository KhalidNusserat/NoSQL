package com.atypon.nosql.database.collection;

import com.atypon.nosql.database.document.Document;
import com.atypon.nosql.database.document.DocumentGenerator;
import com.atypon.nosql.database.io.DefaultIOEngine;
import com.atypon.nosql.database.io.IOEngine;
import com.atypon.nosql.database.utils.ExtraFileUtils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class GenericBasicDocumentsCollection<T extends Document<?>> implements DocumentsCollection<T> {
    private final IOEngine<T> ioEngine;

    private final Path documentsPath;

    private final DocumentGenerator<T> documentGenerator;

    private GenericBasicDocumentsCollection(
            IOEngine<T> ioEngine,
            Path collectionPath,
            DocumentGenerator<T> documentGenerator
    ) {
        this.ioEngine = ioEngine;
        this.documentGenerator = documentGenerator;
        documentsPath = collectionPath;
        try {
            Files.createDirectories(documentsPath);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static <T extends Document<?>> GenericBasicDocumentsCollectionBuilder<T> builder() {
        return new GenericBasicDocumentsCollectionBuilder<>();
    }

    @Override
    public boolean contains(T documentCriteria) {
        return ExtraFileUtils.traverseDirectory(documentsPath)
                .filter(ExtraFileUtils::isJsonFile)
                .map(documentPath -> ioEngine.read(documentPath, documentGenerator))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .anyMatch(documentCriteria::subsetOf);
    }

    @Override
    public Collection<T> getAllThatMatches(T documentCriteria) {
        return ExtraFileUtils.traverseDirectory(documentsPath)
                .filter(ExtraFileUtils::isJsonFile)
                .map(documentPath -> ioEngine.read(documentPath, documentGenerator))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(documentCriteria::subsetOf)
                .toList();
    }

    @Override
    public Collection<T> getAll() {
        return ioEngine.readDirectory(documentsPath, documentGenerator);
    }

    @Override
    public Path addDocument(T addedDocument) {
        return ioEngine.write(addedDocument, documentsPath);
    }

    @Override
    public Path updateDocument(T documentCriteria, T updatedDocument) {
        List<Path> matchingDocumentsPaths = getPathsThatMatch(documentCriteria);
        if (matchingDocumentsPaths.size() > 1) {
            throw new MultipleFilesMatchedException(matchingDocumentsPaths.size());
        } else if (matchingDocumentsPaths.size() == 0) {
            throw new NoSuchDocumentException(documentCriteria);
        } else {
            return ioEngine.update(documentCriteria, matchingDocumentsPaths.get(0));
        }
    }

    private List<Path> getPathsThatMatch(T documentCriteria) {
        return ExtraFileUtils.traverseDirectory(documentsPath)
                .filter(ExtraFileUtils::isJsonFile)
                .filter(path -> ioEngine.read(path, documentGenerator)
                        .map(documentCriteria::subsetOf)
                        .orElse(false))
                .toList();
    }

    @Override
    public int deleteAllThatMatches(T documentCriteria) {
        List<Path> paths = getPathsThatMatch(documentCriteria);
        paths.forEach(ioEngine::delete);
        return paths.size();
    }

    public static class GenericBasicDocumentsCollectionBuilder<T extends Document<?>> {
        private IOEngine<T> ioEngine = new DefaultIOEngine<>();

        private Path documentsPath;

        private DocumentGenerator<T> documentGenerator;

        public GenericBasicDocumentsCollectionBuilder<T> setIoEngine(IOEngine<T> ioEngine) {
            this.ioEngine = ioEngine;
            return this;
        }

        public GenericBasicDocumentsCollectionBuilder<T> setDocumentsPath(Path documentsPath) {
            this.documentsPath = documentsPath;
            return this;
        }

        public GenericBasicDocumentsCollectionBuilder<T> setDocumentsGenerator(
                DocumentGenerator<T> documentsGenerator) {
            this.documentGenerator = documentsGenerator;
            return this;
        }

        public GenericBasicDocumentsCollection<T> build() {
            return new GenericBasicDocumentsCollection<>(ioEngine, documentsPath, documentGenerator);
        }
    }
}
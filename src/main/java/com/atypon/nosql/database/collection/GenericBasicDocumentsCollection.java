package com.atypon.nosql.database.collection;

import com.atypon.nosql.database.document.Document;
import com.atypon.nosql.database.document.DocumentGenerator;
import com.atypon.nosql.database.io.DefaultIOEngine;
import com.atypon.nosql.database.io.IOEngine;
import com.atypon.nosql.database.utils.ExtraFileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class GenericBasicDocumentsCollection<T extends Document<?>> implements DocumentsCollection<T> {
    private final IOEngine ioEngine;

    private final Path documentsPath;

    private final DocumentGenerator<T> documentGenerator;

    private GenericBasicDocumentsCollection(
            IOEngine ioEngine,
            Path collectionPath,
            DocumentGenerator<T> documentGenerator
    ) {
        this.ioEngine = ioEngine;
        this.documentGenerator = documentGenerator;
        documentsPath = collectionPath;
        try {
            Files.createDirectories(documentsPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T extends Document<?>> GenericBasicDocumentsCollectionBuilder<T> builder() {
        return new GenericBasicDocumentsCollectionBuilder<>();
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
    public Path updateDocument(T documentCriteria, T updatedDocument)
            throws NoSuchDocumentException, MultipleFilesMatchedException, IOException
    {
        List<Path> matchingDocumentsPaths = getPathsThatMatch(documentCriteria);
        if (matchingDocumentsPaths.size() > 1) {
            throw new MultipleFilesMatchedException(matchingDocumentsPaths.size());
        } else if (matchingDocumentsPaths.size() == 0) {
            throw new NoSuchDocumentException(documentCriteria);
        } else {
            return ioEngine.update(documentCriteria, matchingDocumentsPaths.get(0));
        }
    }

    @Override
    public void deleteAllThatMatches(T documentCriteria) {
        getPathsThatMatch(documentCriteria).forEach(ioEngine::delete);
    }

    public static class GenericBasicDocumentsCollectionBuilder<T extends Document<?>> {
        private IOEngine ioEngine = new DefaultIOEngine();

        private Path documentsPath;

        private DocumentGenerator<T> documentGenerator;

        public GenericBasicDocumentsCollectionBuilder<T> setIoEngine(IOEngine ioEngine) {
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
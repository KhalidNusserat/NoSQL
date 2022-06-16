package com.atypon.nosql.database;

import com.atypon.nosql.database.collection.GenericIndexedDocumentsCollection;
import com.atypon.nosql.database.collection.IndexedDocumentsCollection;
import com.atypon.nosql.database.document.*;
import com.atypon.nosql.database.index.GenericIndexGenerator;
import com.atypon.nosql.database.io.IOEngine;
import com.atypon.nosql.database.utils.FileUtils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GenericDatabase<T extends Document> implements Database<T> {
    private final Map<String, IndexedDocumentsCollection<T>> collections = new ConcurrentHashMap<>();

    private final Map<String, DocumentSchema<T>> schemas = new ConcurrentHashMap<>();

    private final IOEngine<T> ioEngine;

    private final Path databaseDirectory;

    private final GenericIndexGenerator<T> indexGenerator = new GenericIndexGenerator<>();

    private final DocumentGenerator<T> documentGenerator;

    private final DocumentSchemaGenerator<T> schemaGenerator;

    private final ExecutorService directoriesDeletingService = Executors.newCachedThreadPool();

    private GenericDatabase(
            IOEngine<T> ioEngine,
            Path databaseDirectory,
            DocumentGenerator<T> documentGenerator,
            DocumentSchemaGenerator<T> schemaGenerator
    ) {
        this.ioEngine = ioEngine;
        this.databaseDirectory = databaseDirectory;
        this.documentGenerator = documentGenerator;
        this.schemaGenerator = schemaGenerator;
        createDirectories(databaseDirectory);
        FileUtils.traverseDirectory(databaseDirectory)
                .filter(path -> !path.equals(databaseDirectory))
                .forEach(this::loadCollection);
    }

    public static <T extends Document> GenericDatabaseBuilder<T> builder() {
        return new GenericDatabaseBuilder<>();
    }

    private void createDirectories(Path... directoriesPaths) {
        try {
            for (Path directoryPath : directoriesPaths) {
                Files.createDirectories(directoryPath);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private Path getSchemaPath(Path collectionDirectory) {
        return collectionDirectory.resolve("schema/");
    }

    private void loadCollection(Path collectionDirectory) {
        String collectionName = collectionDirectory.getFileName().toString();
        try {
            Optional<DocumentSchema<T>> schema = loadSchema(getSchemaPath(collectionDirectory));
            if (schema.isPresent()) {
                schemas.put(collectionName, schema.get());
                IndexedDocumentsCollection<T> documentsCollection = GenericIndexedDocumentsCollection.<T>builder()
                        .setDocumentsPath(collectionDirectory)
                        .setDocumentGenerator(documentGenerator)
                        .setIndexGenerator(indexGenerator)
                        .setIOEngine(ioEngine)
                        .build();
                collections.put(collectionName, documentsCollection);
            }
        } catch (InvalidDocumentSchema e) {
            throw new RuntimeException(e);
        }
    }

    private void checkCollectionExists(String collectionName) {
        if (!collections.containsKey(collectionName)) {
            throw new CollectionNotFoundException(collectionName);
        }
    }

    @Override
    public void createCollection(String collectionName, String schemaString) {
        if (collections.containsKey(collectionName)) {
            throw new CollectionAlreadyExists(collectionName);
        }
        Path collectionDirectory = databaseDirectory.resolve(collectionName + "/");
        createDirectories(collectionDirectory, getSchemaPath(collectionDirectory));
        IndexedDocumentsCollection<T> documentsCollection = GenericIndexedDocumentsCollection.<T>builder()
                .setDocumentsPath(collectionDirectory)
                .setDocumentGenerator(documentGenerator)
                .setIndexGenerator(indexGenerator)
                .setIOEngine(ioEngine)
                .build();
        collections.put(collectionName, documentsCollection);
        DocumentSchema<T> documentSchema = createNewSchema(schemaString, getSchemaPath(collectionDirectory));
        schemas.put(collectionName, documentSchema);
    }

    private DocumentSchema<T> createNewSchema(String schemaDocumentString, Path schemaDirectory) {
        T schemaDocument = documentGenerator.createFromString(schemaDocumentString);
        DocumentSchema<T> documentSchema = schemaGenerator.createSchema(schemaDocument);
        ioEngine.write(documentSchema.getAsDocument(), schemaDirectory);
        return documentSchema;
    }

    @Override
    public void removeCollection(String collectionName) {
        checkCollectionExists(collectionName);
        Path collectionDirectory = databaseDirectory.resolve(collectionName + "/");
        collections.remove(collectionName);
        directoriesDeletingService.submit(() -> FileUtils.deleteDirectory(collectionDirectory));
    }

    private Optional<DocumentSchema<T>> loadSchema(Path schemaDirectory)
            throws InvalidDocumentSchema {
        List<T> directoryContents = ioEngine.readDirectory(schemaDirectory, documentGenerator);
        if (directoryContents.size() == 1) {
            return Optional.of(schemaGenerator.createSchema(directoryContents.get(0)));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public IndexedDocumentsCollection<T> get(String collectionName) {
        return collections.get(collectionName);
    }

    @Override
    public Collection<String> getCollectionsNames() {
        return collections.keySet();
    }

    @Override
    public Map<String, Object> getCollectionSchema(String collectionName) {
        checkCollectionExists(collectionName);
        return schemas.get(collectionName).getAsDocument().getAsMap();
    }

    @Override
    public void deleteDatabase() {
        for (String collection : collections.keySet()) {
            removeCollection(collection);
        }
    }

    public static class GenericDatabaseBuilder<T extends Document> {
        private Path databaseDirectory;

        private IOEngine<T> ioEngine;

        private DocumentGenerator<T> documentGenerator;

        private DocumentSchemaGenerator<T> schemaGenerator;

        public GenericDatabaseBuilder<T> setDatabaseDirectory(Path databaseDirectory) {
            this.databaseDirectory = databaseDirectory;
            return this;
        }

        public GenericDatabaseBuilder<T> setIoEngine(IOEngine<T> ioEngine) {
            this.ioEngine = ioEngine;
            return this;
        }

        public GenericDatabaseBuilder<T> setDocumentGenerator(DocumentGenerator<T> documentGenerator) {
            this.documentGenerator = documentGenerator;
            return this;
        }

        public GenericDatabaseBuilder<T> setDocumentSchemaGenerator(DocumentSchemaGenerator<T> schemaGenerator) {
            this.schemaGenerator = schemaGenerator;
            return this;
        }

        public GenericDatabase<T> build() {
            return new GenericDatabase<>(ioEngine, databaseDirectory, documentGenerator, schemaGenerator);
        }
    }
}

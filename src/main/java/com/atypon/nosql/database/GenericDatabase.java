package com.atypon.nosql.database;

import com.atypon.nosql.database.collection.BasicIndexedDocumentsCollection;
import com.atypon.nosql.database.collection.IndexedDocumentsCollection;
import com.atypon.nosql.database.document.*;
import com.atypon.nosql.database.index.DefaultIndexFactory;
import com.atypon.nosql.database.io.IOEngine;
import com.atypon.nosql.database.utils.FileUtils;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GenericDatabase implements Database {
    private final Map<String, IndexedDocumentsCollection> collections = new ConcurrentHashMap<>();

    private final Map<String, DocumentSchema> schemas = new ConcurrentHashMap<>();

    private final IOEngine ioEngine;

    private final Path databaseDirectory;

    private final DefaultIndexFactory indexGenerator = new DefaultIndexFactory();

    private final DocumentFactory documentFactory;

    private final DocumentSchemaFactory schemaGenerator;

    private final ExecutorService directoriesDeletingService = Executors.newCachedThreadPool();

    private GenericDatabase(
            IOEngine ioEngine,
            Path databaseDirectory,
            DocumentFactory documentFactory,
            DocumentSchemaFactory schemaGenerator
    ) {
        this.ioEngine = ioEngine;
        this.databaseDirectory = databaseDirectory;
        this.documentFactory = documentFactory;
        this.schemaGenerator = schemaGenerator;
        FileUtils.createDirectories(databaseDirectory);
        FileUtils.traverseDirectory(databaseDirectory)
                .filter(path -> !path.equals(databaseDirectory))
                .forEach(this::loadCollection);
    }

    public static GenericDatabaseBuilder builder() {
        return new GenericDatabaseBuilder();
    }

    private Path getSchemaPath(Path collectionDirectory) {
        return collectionDirectory.resolve("schema/");
    }

    private void loadCollection(Path collectionDirectory) {
        String collectionName = collectionDirectory.getFileName().toString();
        try {
            Optional<DocumentSchema> schema = loadSchema(getSchemaPath(collectionDirectory));
            if (schema.isPresent()) {
                schemas.put(collectionName, schema.get());
                IndexedDocumentsCollection documentsCollection = BasicIndexedDocumentsCollection.builder()
                        .setDocumentsPath(collectionDirectory)
                        .setDocumentGenerator(documentFactory)
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
        FileUtils.createDirectories(collectionDirectory);
        FileUtils.createDirectories(getSchemaPath(collectionDirectory));
        IndexedDocumentsCollection documentsCollection = BasicIndexedDocumentsCollection.builder()
                .setDocumentsPath(collectionDirectory)
                .setDocumentGenerator(documentFactory)
                .setIndexGenerator(indexGenerator)
                .setIOEngine(ioEngine)
                .build();
        collections.put(collectionName, documentsCollection);
        DocumentSchema documentSchema = createNewSchema(schemaString, getSchemaPath(collectionDirectory));
        schemas.put(collectionName, documentSchema);
    }

    private DocumentSchema createNewSchema(String schemaDocumentString, Path schemaDirectory) {
        Document schemaDocument = documentFactory.createFromString(schemaDocumentString);
        DocumentSchema documentSchema = schemaGenerator.createSchema(schemaDocument);
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

    private Optional<DocumentSchema> loadSchema(Path schemaDirectory) {
        List<Document> directoryContents = ioEngine.readDirectory(schemaDirectory);
        if (directoryContents.size() == 1) {
            return Optional.of(schemaGenerator.createSchema(directoryContents.get(0)));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public IndexedDocumentsCollection get(String collectionName) {
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

    public static class GenericDatabaseBuilder {
        private Path databaseDirectory;

        private IOEngine ioEngine;

        private DocumentFactory documentFactory;

        private DocumentSchemaFactory schemaGenerator;

        public GenericDatabaseBuilder setDatabaseDirectory(Path databaseDirectory) {
            this.databaseDirectory = databaseDirectory;
            return this;
        }

        public GenericDatabaseBuilder setIoEngine(IOEngine ioEngine) {
            this.ioEngine = ioEngine;
            return this;
        }

        public GenericDatabaseBuilder setDocumentGenerator(DocumentFactory documentFactory) {
            this.documentFactory = documentFactory;
            return this;
        }

        public GenericDatabaseBuilder setDocumentSchemaGenerator(DocumentSchemaFactory schemaGenerator) {
            this.schemaGenerator = schemaGenerator;
            return this;
        }

        public GenericDatabase build() {
            return new GenericDatabase(ioEngine, databaseDirectory, documentFactory, schemaGenerator);
        }
    }
}

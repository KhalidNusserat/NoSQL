package com.atypon.nosql.database;

import com.atypon.nosql.database.collection.*;
import com.atypon.nosql.database.document.*;
import com.atypon.nosql.database.gsondocument.FieldsDoNotMatchException;
import com.atypon.nosql.database.index.GenericIndexGenerator;
import com.atypon.nosql.database.io.IOEngine;
import com.atypon.nosql.database.utils.ExtraFileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GenericDatabase<T extends Document<?>> implements Database {
    private final Map<String, IndexedDocumentsCollection<T>> collections = new ConcurrentHashMap<>();

    private final Map<String, DocumentSchema<T>> schemas = new ConcurrentHashMap<>();

    private final IOEngine ioEngine;

    private final Path databaseDirectory;

    private final GenericIndexGenerator<T> indexGenerator = new GenericIndexGenerator<>();

    private final DocumentGenerator<T> documentGenerator;

    private final DocumentSchemaGenerator<T> schemaGenerator;

    private final ExecutorService directoriesDeletingService = Executors.newCachedThreadPool();

    public GenericDatabase(
            IOEngine ioEngine,
            Path databaseDirectory,
            DocumentGenerator<T> documentGenerator,
            DocumentSchemaGenerator<T> schemaGenerator
    ) {
        this.ioEngine = ioEngine;
        this.databaseDirectory = databaseDirectory;
        this.documentGenerator = documentGenerator;
        this.schemaGenerator = schemaGenerator;
        createDirectories(databaseDirectory);
        try {
            Files.walk(databaseDirectory, 1)
                    .filter(path -> !path.equals(databaseDirectory))
                    .forEach(this::loadCollection);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createDirectories(Path... directoriesPaths) {
        try {
            for (Path directoryPath : directoriesPaths) {
                Files.createDirectories(directoryPath);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Path getDocumentsPath(Path collectionDirectory) {
        return collectionDirectory.resolve("documents/");
    }

    private Path getIndexesPath(Path collectionDirectory) {
        return collectionDirectory.resolve("indexes/");
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
                        .create();
                collections.put(collectionName, documentsCollection);
            }
        } catch (InvalidDocumentSchema e) {
            throw new RuntimeException(e);
        }
    }

    private void checkCollectionExists(String collectionName) throws CollectionNotFoundException {
        if (!collections.containsKey(collectionName)) {
            throw new CollectionNotFoundException(collectionName);
        }
    }

    @Override
    public void createCollection(String collectionName, String schemaString)
            throws InvalidDocumentSchema, CollectionAlreadyExists {
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
                .create();
        collections.put(collectionName, documentsCollection);
        DocumentSchema<T> documentSchema = createNewSchema(schemaString, getSchemaPath(collectionDirectory));
        schemas.put(collectionName, documentSchema);
    }

    private DocumentSchema<T> createNewSchema(String schemaDocumentString, Path schemaDirectory)
            throws InvalidDocumentSchema {
        T schemaDocument = documentGenerator.createFromString(schemaDocumentString);
        DocumentSchema<T> documentSchema = schemaGenerator.createSchema(schemaDocument);
        try {
            ioEngine.write(documentSchema.getAsDocument(), schemaDirectory);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return documentSchema;
    }

    @Override
    public void deleteCollection(String collectionName) throws CollectionNotFoundException {
        checkCollectionExists(collectionName);
        Path collectionDirectory = databaseDirectory.resolve(collectionName + "/");
        collections.remove(collectionName);
        directoriesDeletingService.submit(() -> ExtraFileUtils.deleteDirectory(collectionDirectory));
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
    public void addDocument(String collectionName, String documentString)
            throws IOException, DocumentSchemaViolationException, CollectionNotFoundException {
        checkCollectionExists(collectionName);
        T document = documentGenerator.createFromString(documentString);
        if (schemas.get(collectionName).validate(document)) {
            collections.get(collectionName).addDocument(documentGenerator.appendId(document));
        } else {
            throw new DocumentSchemaViolationException();
        }
    }

    @Override
    public Collection<Map<String, Object>> readDocuments(String collectionName, String matchDocumentString)
            throws FieldsDoNotMatchException, IOException, CollectionNotFoundException {
        checkCollectionExists(collectionName);
        T matchDocument = documentGenerator.createFromString(matchDocumentString);
        return collections.get(collectionName).getAllThatMatches(matchDocument).stream()
                .map(Document::getAsMap)
                .toList();
    }

    @Override
    public void updateDocument(String collectionName, String documentID, String updatedDocumentString)
            throws MultipleFilesMatchedException, IOException, NoSuchDocumentException,
            DocumentSchemaViolationException, CollectionNotFoundException {
        checkCollectionExists(collectionName);
        T matchId = documentGenerator.createFromString(String.format("{_id: \"%s\"}", documentID));
        T updatedDocument = documentGenerator.createFromString(updatedDocumentString);
        if (schemas.get(collectionName).validate(updatedDocument)) {
            collections.get(collectionName).updateDocument(matchId, updatedDocument);
        } else {
            throw new DocumentSchemaViolationException();
        }
    }

    @Override
    public void deleteDocuments(String collectionName, String matchDocumentString)
            throws FieldsDoNotMatchException, IOException, CollectionNotFoundException {
        checkCollectionExists(collectionName);
        T matchDocument = documentGenerator.createFromString(matchDocumentString);
        collections.get(collectionName).deleteAllThatMatches(matchDocument);
    }

    @Override
    public Collection<Map<String, Object>> getCollectionIndexes(String collectionName)
            throws CollectionNotFoundException {
        checkCollectionExists(collectionName);
        return collections.get(collectionName).getIndexes().stream()
                .map(Document::getAsMap).
                toList();
    }

    @Override
    public void createIndex(String collectionName, String indexDocumentString)
            throws IOException, CollectionNotFoundException {
        checkCollectionExists(collectionName);
        T indexDocument = documentGenerator.createFromString(indexDocumentString);
        collections.get(collectionName).createIndex(indexDocument);
    }

    @Override
    public void deleteIndex(String collectionName, String indexDocumentString)
            throws CollectionNotFoundException, NoSuchIndexException {
        checkCollectionExists(collectionName);
        T indexDocument = documentGenerator.createFromString(indexDocumentString);
        collections.get(collectionName).deleteIndex(indexDocument);
    }

    @Override
    public Collection<String> getCollectionsNames() {
        return collections.keySet();
    }

    @Override
    public Map<String, Object> getCollectionSchema(String collectionName) throws CollectionNotFoundException {
        checkCollectionExists(collectionName);
        return schemas.get(collectionName).getAsDocument().getAsMap();
    }
}

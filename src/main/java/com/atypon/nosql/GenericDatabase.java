package com.atypon.nosql;

import com.atypon.nosql.collection.*;
import com.atypon.nosql.document.*;
import com.atypon.nosql.gsondocument.FieldsDoNotMatchException;
import com.atypon.nosql.index.GenericIndexGenerator;
import com.atypon.nosql.io.IOEngine;
import com.atypon.nosql.keywordsparser.InvalidKeywordException;
import com.atypon.nosql.utils.ExtraFileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GenericDatabase<T extends Document<?>> implements Database {
    private final Map<String, IndexedDocumentsCollection<T>> collections = new ConcurrentHashMap<>();

    private final Map<String, DocumentSchema<T>> schemas = new ConcurrentHashMap<>();

    private final IOEngine ioEngine;

    private final Path collectionsDirectory;

    private final GenericIndexGenerator<T> indexGenerator = new GenericIndexGenerator<>();

    private final DocumentGenerator<T> documentGenerator;

    private final DocumentSchemaGenerator<T> schemaGenerator;

    private final ExecutorService directoriesDeletingService = Executors.newCachedThreadPool();

    public GenericDatabase(
            IOEngine ioEngine,
            Path collectionsDirectory,
            DocumentGenerator<T> documentGenerator,
            DocumentSchemaGenerator<T> schemaGenerator
    ) {
        this.ioEngine = ioEngine;
        this.collectionsDirectory = collectionsDirectory;
        this.documentGenerator = documentGenerator;
        this.schemaGenerator = schemaGenerator;
        createDirectories(collectionsDirectory);
        try {
            Files.walk(collectionsDirectory, 1).forEach(this::loadCollection);
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

    private void loadCollection(Path collectionDirectory) {
        String collectionName = collectionDirectory.getFileName().toString();
        Path documentsDirectory = collectionDirectory.resolve("documents/");
        Path schemaDirectory = collectionDirectory.resolve("schema/");
        try {
            schemas.put(collectionName, loadSchema(schemaDirectory));
        } catch (InvalidKeywordException | InvalidDocumentSchema e) {
            throw new RuntimeException(e);
        }
        IndexedDocumentsCollection<T> documentsCollection = GenericIndexedDocumentsCollection.<T>builder()
                .setDocumentsPath(documentsDirectory)
                .setDocumentGenerator(documentGenerator)
                .setIndexGenerator(indexGenerator)
                .setIOEngine(ioEngine)
                .create();
        collections.put(collectionName, documentsCollection);
    }

    private void checkCollectionExists(String collectionName) throws CollectionNotFoundException {
        if (!collections.containsKey(collectionName)) {
            throw new CollectionNotFoundException(collectionName);
        }
    }

    @Override
    public void createCollection(String collectionName, String schemaString)
            throws InvalidKeywordException, InvalidDocumentSchema, CollectionAlreadyExists {
        if (collections.containsKey(collectionName)) {
            throw new CollectionAlreadyExists(collectionName);
        }
        Path collectionDirectory = collectionsDirectory.resolve(collectionName + "/");
        Path documentsDirectory = collectionDirectory.resolve("documents/");
        Path schemaDirectory = collectionDirectory.resolve("schema/");
        createDirectories(documentsDirectory, schemaDirectory);
        IndexedDocumentsCollection<T> documentsCollection = GenericIndexedDocumentsCollection.<T>builder()
                .setDocumentsPath(documentsDirectory)
                .setDocumentGenerator(documentGenerator)
                .setIndexGenerator(indexGenerator)
                .setIOEngine(ioEngine)
                .create();
        collections.put(collectionName, documentsCollection);
        DocumentSchema<T> documentSchema = createNewSchema(schemaString, schemaDirectory);
        schemas.put(collectionName, documentSchema);
    }

    private DocumentSchema<T> createNewSchema(String schemaDocumentString, Path schemaDirectory)
            throws InvalidKeywordException, InvalidDocumentSchema
    {
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
    public void removeCollection(String collectionName) throws CollectionNotFoundException {
        checkCollectionExists(collectionName);
        Path collectionDirectory = collectionsDirectory.resolve(collectionName + "/");
        collections.remove(collectionName);
        directoriesDeletingService.submit(() -> ExtraFileUtils.deleteDirectory(collectionDirectory));
    }

    private DocumentSchema<T> loadSchema(Path schemaDirectory) throws InvalidKeywordException, InvalidDocumentSchema {
        Optional<T> schemaDocument = ioEngine.read(schemaDirectory, documentGenerator);
        return schemaGenerator.createSchema(schemaDocument.orElseThrow());
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
    public Collection<String> readDocuments(String collectionName, String matchDocumentString)
            throws FieldsDoNotMatchException, IOException, CollectionNotFoundException {
        checkCollectionExists(collectionName);
        T matchDocument = documentGenerator.createFromString(matchDocumentString);
        return collections.get(collectionName).getAllThatMatches(matchDocument).stream()
                .map(Document::toString)
                .toList();
    }

    @Override
    public void updateDocument(String collectionName, String documentID, String updatedDocumentString)
            throws MultipleFilesMatchedException, IOException, NoSuchDocumentException,
            DocumentSchemaViolationException, CollectionNotFoundException {
        checkCollectionExists(collectionName);
        T matchId = documentGenerator.createFromString(String.format("{_id: %s}", documentID));
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
}

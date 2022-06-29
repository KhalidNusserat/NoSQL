package com.atypon.nosql.collection;

import com.atypon.nosql.document.Document;
import com.atypon.nosql.document.DocumentSchema;
import com.atypon.nosql.idgenerator.IdGenerator;
import com.atypon.nosql.index.IndexesCollectionFactory;
import com.atypon.nosql.storage.StorageEngine;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;

@Component
public class DefaultIndexedDocumentsCollectionFactory implements IndexedDocumentsCollectionFactory {
    private final StorageEngine storageEngine;

    private final BasicDocumentsCollectionFactory documentsCollectionFactory;

    private final IndexesCollectionFactory indexesCollectionFactory;

    private final IdGenerator idGenerator;

    public DefaultIndexedDocumentsCollectionFactory(
            StorageEngine storageEngine,
            BasicDocumentsCollectionFactory documentsCollectionFactory,
            IndexesCollectionFactory indexesCollectionFactory,
            IdGenerator idGenerator) {
        this.storageEngine = storageEngine;
        this.documentsCollectionFactory = documentsCollectionFactory;
        this.indexesCollectionFactory = indexesCollectionFactory;
        this.idGenerator = idGenerator;
    }

    @Override
    public IndexedDocumentsCollection createCollection(Path collectionPath, Document schemaDocument) {
        DocumentSchema documentSchema = DocumentSchema.createFromDocument(schemaDocument);
        return new DefaultIndexedDocumentsCollection(
                collectionPath,
                storageEngine,
                documentsCollectionFactory,
                indexesCollectionFactory,
                documentSchema,
                idGenerator);
    }

    @Override
    public IndexedDocumentsCollection createCollection(Path collectionPath) {
        Path schemaDirectory = collectionPath.resolve("schema/");
        List<Document> schemaDocuments = storageEngine.readDocumentsDirectory(schemaDirectory);
        if (schemaDocuments.size() == 0) {
            throw new SchemaNotFoundException();
        } else if (schemaDocuments.size() > 1) {
            throw new MultipleSchemasException();
        }
        DocumentSchema documentSchema = DocumentSchema.createFromDocument(schemaDocuments.get(0));
        return new DefaultIndexedDocumentsCollection(
                collectionPath,
                storageEngine,
                documentsCollectionFactory,
                indexesCollectionFactory,
                documentSchema,
                idGenerator);
    }
}

package com.atypon.nosql.collection;

import com.atypon.nosql.document.Document;
import com.atypon.nosql.document.DocumentSchema;
import com.atypon.nosql.document.DocumentSchemaFactory;
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

    private final DocumentSchemaFactory schemaFactory;

    public DefaultIndexedDocumentsCollectionFactory(
            StorageEngine storageEngine,
            BasicDocumentsCollectionFactory documentsCollectionFactory,
            IndexesCollectionFactory indexesCollectionFactory,
            DocumentSchemaFactory schemaFactory) {
        this.storageEngine = storageEngine;
        this.documentsCollectionFactory = documentsCollectionFactory;
        this.indexesCollectionFactory = indexesCollectionFactory;
        this.schemaFactory = schemaFactory;
    }

    @Override
    public IndexedDocumentsCollection createCollection(Path collectionPath, Document schemaDocument) {
        DocumentSchema documentSchema = schemaFactory.createSchema(schemaDocument);
        return new DefaultIndexedDocumentsCollection(
                collectionPath,
                storageEngine,
                documentsCollectionFactory,
                indexesCollectionFactory,
                documentSchema
        );
    }

    @Override
    public IndexedDocumentsCollection createCollection(Path collectionPath) {
        Path schemaDirectory = collectionPath.resolve("schema/");
        List<Document> schemaDocuments = storageEngine.readDirectory(schemaDirectory);
        if (schemaDocuments.size() == 0) {
            throw new SchemaNotFoundException();
        } else if (schemaDocuments.size() > 1) {
            throw new MultipleSchemasException();
        }
        DocumentSchema documentSchema = schemaFactory.createSchema(schemaDocuments.get(0));
        return new DefaultIndexedDocumentsCollection(
                collectionPath,
                storageEngine,
                documentsCollectionFactory,
                indexesCollectionFactory,
                documentSchema
        );
    }
}

package com.atypon.nosql.database.collection;

import com.atypon.nosql.database.document.Document;
import com.atypon.nosql.database.document.DocumentSchema;
import com.atypon.nosql.database.document.DocumentSchemaFactory;
import com.atypon.nosql.database.document.ObjectIdGenerator;
import com.atypon.nosql.database.index.IndexesCollectionFactory;
import com.atypon.nosql.database.io.IOEngine;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;

@Component
public class DefaultIndexedDocumentsCollectionFactory implements IndexedDocumentsCollectionFactory {
    private final IOEngine ioEngine;

    private final ObjectIdGenerator idGenerator;

    private final BasicDocumentsCollectionFactory documentsCollectionFactory;

    private final IndexesCollectionFactory indexesCollectionFactory;

    private final DocumentSchemaFactory schemaFactory;

    public DefaultIndexedDocumentsCollectionFactory(
            IOEngine ioEngine,
            ObjectIdGenerator idGenerator,
            BasicDocumentsCollectionFactory documentsCollectionFactory,
            IndexesCollectionFactory indexesCollectionFactory,
            DocumentSchemaFactory schemaFactory) {
        this.ioEngine = ioEngine;
        this.idGenerator = idGenerator;
        this.documentsCollectionFactory = documentsCollectionFactory;
        this.indexesCollectionFactory = indexesCollectionFactory;
        this.schemaFactory = schemaFactory;
    }

    @Override
    public IndexedDocumentsCollection createCollection(Path collectionPath, Document schemaDocument) {
        DocumentSchema documentSchema = schemaFactory.createSchema(schemaDocument);
        return new DefaultIndexedDocumentsCollection(
                collectionPath,
                ioEngine,
                idGenerator,
                documentsCollectionFactory,
                indexesCollectionFactory,
                documentSchema
        );
    }

    @Override
    public IndexedDocumentsCollection createCollection(Path collectionPath) {
        Path schemaDirectory = collectionPath.resolve("schema/");
        List<Document> schemaDocuments = ioEngine.readDirectory(schemaDirectory);
        if (schemaDocuments.size() == 0) {
            throw new SchemaNotFoundException();
        } else if (schemaDocuments.size() > 1) {
            throw new MultipleSchemasException();
        }
        DocumentSchema documentSchema = schemaFactory.createSchema(schemaDocuments.get(0));
        return new DefaultIndexedDocumentsCollection(
                collectionPath,
                ioEngine,
                idGenerator,
                documentsCollectionFactory,
                indexesCollectionFactory,
                documentSchema
        );
    }
}

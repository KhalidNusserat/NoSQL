package com.atypon.nosql.database;

import com.atypon.nosql.database.collection.IndexedDocumentsCollectionFactory;
import com.atypon.nosql.database.document.DocumentFactory;
import com.atypon.nosql.database.document.DocumentSchemaFactory;
import com.atypon.nosql.database.io.IOEngine;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class DefaultDatabaseFactory implements DatabaseFactory {
    private final IOEngine ioEngine;

    private final DocumentFactory documentFactory;

    private final DocumentSchemaFactory schemaGenerator;

    private final IndexedDocumentsCollectionFactory collectionFactory;

    private DefaultDatabaseFactory(
            IOEngine ioEngine,
            DocumentFactory documentFactory,
            DocumentSchemaFactory schemaFactory,
            IndexedDocumentsCollectionFactory collectionFactory) {
        this.ioEngine = ioEngine;
        this.documentFactory = documentFactory;
        this.schemaGenerator = schemaFactory;
        this.collectionFactory = collectionFactory;
    }

    public static DefaultDatabaseFactoryBuilder builder() {
        return new DefaultDatabaseFactoryBuilder();
    }

    @Override
    public Database create(Path databaseDirectory) {
        return DefaultDatabase.builder()
                .setDatabaseDirectory(databaseDirectory)
                .setDocumentGenerator(documentFactory)
                .setDocumentSchemaFactory(schemaGenerator)
                .setIoEngine(ioEngine)
                .setCollectionFactory(collectionFactory)
                .build();
    }

    public static class DefaultDatabaseFactoryBuilder {
        private IOEngine ioEngine;

        private DocumentFactory documentFactory;

        private DocumentSchemaFactory schemaGenerator;

        private IndexedDocumentsCollectionFactory collectionFactory;

        public DefaultDatabaseFactoryBuilder setIoEngine(IOEngine ioEngine) {
            this.ioEngine = ioEngine;
            return this;
        }

        public DefaultDatabaseFactoryBuilder setDocumentFactory(DocumentFactory documentFactory) {
            this.documentFactory = documentFactory;
            return this;
        }

        public DefaultDatabaseFactoryBuilder setSchemaFactory(DocumentSchemaFactory schemaGenerator) {
            this.schemaGenerator = schemaGenerator;
            return this;
        }

        public DefaultDatabaseFactoryBuilder setIndexFactory(IndexedDocumentsCollectionFactory collectionFactory) {
            this.collectionFactory = collectionFactory;
            return this;
        }

        public DefaultDatabaseFactory build() {
            return new DefaultDatabaseFactory(
                    ioEngine,
                    documentFactory,
                    schemaGenerator,
                    collectionFactory
            );
        }
    }
}

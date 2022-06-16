package com.atypon.nosql.database;

import com.atypon.nosql.database.document.DocumentFactory;
import com.atypon.nosql.database.document.DocumentSchemaFactory;
import com.atypon.nosql.database.io.IOEngine;

import java.nio.file.Path;

public class GenericDatabaseFactory implements DatabaseFactory {
    private final IOEngine ioEngine;

    private final DocumentFactory documentFactory;

    private final DocumentSchemaFactory schemaGenerator;

    private GenericDatabaseFactory(
            IOEngine ioEngine,
            DocumentFactory documentFactory,
            DocumentSchemaFactory schemaGenerator) {
        this.ioEngine = ioEngine;
        this.documentFactory = documentFactory;
        this.schemaGenerator = schemaGenerator;
    }

    public static GenericDatabaseGeneratorBuilder builder() {
        return new GenericDatabaseGeneratorBuilder();
    }

    @Override
    public Database create(Path databaseDirectory) {
        return GenericDatabase.builder()
                .setDatabaseDirectory(databaseDirectory)
                .setDocumentGenerator(documentFactory)
                .setDocumentSchemaGenerator(schemaGenerator)
                .setIoEngine(ioEngine)
                .build();
    }

    public static class GenericDatabaseGeneratorBuilder {
        private IOEngine ioEngine;

        private DocumentFactory documentFactory;

        private DocumentSchemaFactory schemaGenerator;

        public GenericDatabaseGeneratorBuilder setIoEngine(IOEngine ioEngine) {
            this.ioEngine = ioEngine;
            return this;
        }

        public GenericDatabaseGeneratorBuilder setDocumentGenerator(DocumentFactory documentFactory) {
            this.documentFactory = documentFactory;
            return this;
        }

        public GenericDatabaseGeneratorBuilder setSchemaGenerator(DocumentSchemaFactory schemaGenerator) {
            this.schemaGenerator = schemaGenerator;
            return this;
        }

        public GenericDatabaseFactory build() {
            return new GenericDatabaseFactory(ioEngine, documentFactory, schemaGenerator);
        }
    }
}

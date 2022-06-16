package com.atypon.nosql.database;

import com.atypon.nosql.database.document.Document;
import com.atypon.nosql.database.document.DocumentGenerator;
import com.atypon.nosql.database.document.DocumentSchemaGenerator;
import com.atypon.nosql.database.io.IOEngine;

import java.nio.file.Path;

public class GenericDatabaseGenerator<T extends Document> implements DatabaseGenerator<T> {
    private final IOEngine<T> ioEngine;

    private final DocumentGenerator<T> documentGenerator;

    private final DocumentSchemaGenerator<T> schemaGenerator;

    private GenericDatabaseGenerator(
            IOEngine<T> ioEngine,
            DocumentGenerator<T> documentGenerator,
            DocumentSchemaGenerator<T> schemaGenerator) {
        this.ioEngine = ioEngine;
        this.documentGenerator = documentGenerator;
        this.schemaGenerator = schemaGenerator;
    }

    public static <T extends Document> GenericDatabaseGeneratorBuilder<T> builder() {
        return new GenericDatabaseGeneratorBuilder<>();
    }

    @Override
    public Database<T> create(Path databaseDirectory) {
        return GenericDatabase.<T>builder()
                .setDatabaseDirectory(databaseDirectory)
                .setDocumentGenerator(documentGenerator)
                .setDocumentSchemaGenerator(schemaGenerator)
                .setIoEngine(ioEngine)
                .build();
    }

    public static class GenericDatabaseGeneratorBuilder<T extends Document> {
        private IOEngine<T> ioEngine;

        private DocumentGenerator<T> documentGenerator;

        private DocumentSchemaGenerator<T> schemaGenerator;

        public GenericDatabaseGeneratorBuilder<T> setIoEngine(IOEngine<T> ioEngine) {
            this.ioEngine = ioEngine;
            return this;
        }

        public GenericDatabaseGeneratorBuilder<T> setDocumentGenerator(DocumentGenerator<T> documentGenerator) {
            this.documentGenerator = documentGenerator;
            return this;
        }

        public GenericDatabaseGeneratorBuilder<T> setSchemaGenerator(DocumentSchemaGenerator<T> schemaGenerator) {
            this.schemaGenerator = schemaGenerator;
            return this;
        }

        public GenericDatabaseGenerator<T> build() {
            return new GenericDatabaseGenerator<>(ioEngine, documentGenerator, schemaGenerator);
        }
    }
}

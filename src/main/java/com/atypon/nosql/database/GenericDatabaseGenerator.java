package com.atypon.nosql.database;

import com.atypon.nosql.database.document.Document;
import com.atypon.nosql.database.document.DocumentGenerator;
import com.atypon.nosql.database.document.DocumentSchemaGenerator;
import com.atypon.nosql.database.io.IOEngine;

import java.nio.file.Path;

public class GenericDatabaseGenerator<T extends Document<?>> implements DatabaseGenerator {
    private final IOEngine<T> ioEngine;

    private final DocumentGenerator<T> documentGenerator;

    private final DocumentSchemaGenerator<T> schemaGenerator;

    public GenericDatabaseGenerator(
            IOEngine<T> ioEngine,
            DocumentGenerator<T> documentGenerator,
            DocumentSchemaGenerator<T> schemaGenerator) {
        this.ioEngine = ioEngine;
        this.documentGenerator = documentGenerator;
        this.schemaGenerator = schemaGenerator;
    }

    @Override
    public Database create(Path databaseDirectory) {
        return GenericDatabase.<T>builder()
                .setDatabaseDirectory(databaseDirectory)
                .setDocumentGenerator(documentGenerator)
                .setDocumentSchemaGenerator(schemaGenerator)
                .setIoEngine(ioEngine)
                .build();
    }
}

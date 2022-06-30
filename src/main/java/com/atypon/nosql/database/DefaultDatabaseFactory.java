package com.atypon.nosql.database;

import com.atypon.nosql.collection.IndexedDocumentsCollectionFactory;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@ToString
@Component
public class DefaultDatabaseFactory implements DatabaseFactory {

    private final IndexedDocumentsCollectionFactory collectionFactory;

    private DefaultDatabaseFactory(IndexedDocumentsCollectionFactory collectionFactory) {
        this.collectionFactory = collectionFactory;
    }

    @Override
    public Database create(Path databaseDirectory) {
        return new DefaultDatabase(databaseDirectory, collectionFactory);
    }
}

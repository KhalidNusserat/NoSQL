package com.atypon.nosql.database;

public class CollectionNotFoundException extends Exception {
    public CollectionNotFoundException(String collectionName) {
        super("Collection not found: " + collectionName);
    }
}

package com.atypon.nosql;

public class CollectionNotFoundException extends Exception {
    public CollectionNotFoundException(String collectionName) {
        super("Collection not found: " + collectionName);
    }
}

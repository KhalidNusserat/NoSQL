package com.atypon.nosql.database;

public class CollectionAlreadyExists extends Exception {
    public CollectionAlreadyExists(String message) {
        super("Collection already exists: " + message);
    }
}

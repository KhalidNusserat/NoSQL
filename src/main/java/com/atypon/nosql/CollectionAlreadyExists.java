package com.atypon.nosql;

public class CollectionAlreadyExists extends Exception {
    public CollectionAlreadyExists(String message) {
        super("Collection already exists: " + message);
    }
}

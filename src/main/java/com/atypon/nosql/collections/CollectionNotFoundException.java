package com.atypon.nosql.collections;

public class CollectionNotFoundException extends StoreException {
    public CollectionNotFoundException(String collection) {
        super("Collection not found: " + collection);
    }
}

package com.atypon.nosql.collection;

public class CollectionNotFoundException extends StoreException {
    public CollectionNotFoundException(String collection) {
        super("Collection not found: " + collection);
    }
}

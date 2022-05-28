package com.atypon.nosql.store;

public class CollectionNotFoundException extends StoreException {
    public CollectionNotFoundException(String collection) {
        super("Collection not found: " + collection);
    }
}

package com.atypon.nosql.store;

public class CollectionNotFoundException extends StoreException {
    public CollectionNotFoundException(String message) {
        super(message);
    }
}

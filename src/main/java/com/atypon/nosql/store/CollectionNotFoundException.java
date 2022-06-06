package com.atypon.nosql.store;

import com.atypon.nosql.store.StoreException;

public class CollectionNotFoundException extends StoreException {
    public CollectionNotFoundException(String collection) {
        super("Collection not found: " + collection);
    }
}

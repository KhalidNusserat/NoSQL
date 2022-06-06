package com.atypon.nosql.collection;

public class ItemNotFoundException extends Exception {
    public ItemNotFoundException(String id) {
        super("Item not found: " + id);
    }
}

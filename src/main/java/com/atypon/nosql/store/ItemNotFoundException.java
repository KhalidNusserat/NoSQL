package com.atypon.nosql.store;

public class ItemNotFoundException extends Exception {
    public ItemNotFoundException(String id) {
        super("Item not found: " + id);
    }
}

package com.atypon.nosql.store.exceptions;

public class ItemNotFoundException extends Exception {
    public ItemNotFoundException(String id) {
        super("Item not found: " + id);
    }
}

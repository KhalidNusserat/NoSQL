package com.atypon.nosql.collections;

public class ItemNotFoundException extends Exception {
    public ItemNotFoundException(String id) {
        super("Item not found: " + id);
    }
}

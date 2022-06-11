package com.atypon.nosql.collection;

import com.atypon.nosql.document.Document;

public class NoSuchDocumentException extends Exception {
    public <T extends Document<?>> NoSuchDocumentException(T document) {
        super("Item not found: " + document);
    }
}

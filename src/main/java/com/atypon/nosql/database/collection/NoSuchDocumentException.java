package com.atypon.nosql.database.collection;

import com.atypon.nosql.database.document.Document;

public class NoSuchDocumentException extends Exception {
    public <T extends Document<?>> NoSuchDocumentException(T document) {
        super("Item not found: " + document);
    }
}

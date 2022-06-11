package com.atypon.nosql.collection;

import com.atypon.nosql.document.Document;

public class NoSuchIndexException extends Exception {
    public <T extends Document<?>> NoSuchIndexException(T matchDocument) {
        super("No such index exists: " + matchDocument);
    }
}

package com.atypon.nosql.database.collection;

import com.atypon.nosql.database.document.Document;

public class NoSuchIndexException extends Exception {
    public <T extends Document<?>> NoSuchIndexException(T matchDocument) {
        super("No such index exists: " + matchDocument);
    }
}

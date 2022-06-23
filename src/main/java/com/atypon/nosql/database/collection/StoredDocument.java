package com.atypon.nosql.database.collection;

import com.atypon.nosql.database.document.Document;

import java.nio.file.Path;

public record StoredDocument(Document document, Path path) {
    public StoredDocument(Document document, Path path) {
        this.document = document;
        this.path = path;
    }

    public static StoredDocument createStoredDocument(Document document, Path path) {
        return new StoredDocument(document, path);
    }
}

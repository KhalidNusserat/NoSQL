package com.atypon.nosql.database.document;

public interface DocumentFactory {
    Document createFromString(String src);

    Document appendId(Document document);
}

package com.atypon.nosql.database.document;

public interface DocumentIdGenerator {
    String newId(Document document);
}

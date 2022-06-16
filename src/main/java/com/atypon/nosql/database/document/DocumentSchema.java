package com.atypon.nosql.database.document;

public interface DocumentSchema {
    boolean validate(Document document);

    Document getAsDocument();
}

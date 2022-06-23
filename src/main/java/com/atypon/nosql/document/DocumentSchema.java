package com.atypon.nosql.document;

public interface DocumentSchema {
    boolean validate(Document document);

    Document getAsDocument();
}

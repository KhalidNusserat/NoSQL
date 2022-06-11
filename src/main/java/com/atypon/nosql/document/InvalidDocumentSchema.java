package com.atypon.nosql.document;

public class InvalidDocumentSchema extends Exception {
    public InvalidDocumentSchema(String message) {
        super("Invalid document schema syntax: " + message);
    }
}

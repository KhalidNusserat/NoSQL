package com.atypon.document;

public class InvalidFieldNameException extends Exception {
    public InvalidFieldNameException() {
    }

    public InvalidFieldNameException(String field) {
        super(String.format("The field name \"%s\" is invalid", field));
    }
}

package com.atypon.document;

public class IllegalFieldNameException extends Exception {
    public IllegalFieldNameException() {
    }

    public IllegalFieldNameException(String field) {
        super(String.format("The field name \"%s\" is invalid", field));
    }
}

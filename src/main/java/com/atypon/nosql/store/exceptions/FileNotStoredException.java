package com.atypon.nosql.store.exceptions;

import java.io.IOException;

public class FileNotStoredException extends IOException {
    public FileNotStoredException(String message) {
        super(message);
    }
}

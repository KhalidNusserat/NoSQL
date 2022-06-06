package com.atypon.nosql.collection;

import java.io.IOException;

public class FileNotStoredException extends IOException {
    public FileNotStoredException(String message) {
        super(message);
    }
}

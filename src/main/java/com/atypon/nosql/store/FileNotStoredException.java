package com.atypon.nosql.store;

import java.io.IOException;

public class FileNotStoredException extends IOException {
    public FileNotStoredException(String message) {
        super(message);
    }
}

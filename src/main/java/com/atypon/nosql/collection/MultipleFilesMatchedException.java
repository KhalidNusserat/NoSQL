package com.atypon.nosql.collection;

public class MultipleFilesMatchedException extends Exception {
    public MultipleFilesMatchedException(int size) {
        super("Expected to match 1 file, instead matched " + size + " files");
    }
}

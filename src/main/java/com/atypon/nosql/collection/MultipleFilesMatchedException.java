package com.atypon.nosql.collection;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MultipleFilesMatchedException extends RuntimeException {
    public MultipleFilesMatchedException(int size) {
        super("Expected to match 1 file, instead matched " + size + " files");
    }
}

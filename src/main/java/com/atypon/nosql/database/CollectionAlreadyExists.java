package com.atypon.nosql.database;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class CollectionAlreadyExists extends RuntimeException {
    public CollectionAlreadyExists(String collection) {
        super("Collection already exists: " + collection);
    }
}

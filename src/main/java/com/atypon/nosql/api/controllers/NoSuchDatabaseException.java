package com.atypon.nosql.api.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoSuchDatabaseException extends RuntimeException {
    public NoSuchDatabaseException(String database) {
        super("Database not found: " + database);
    }
}

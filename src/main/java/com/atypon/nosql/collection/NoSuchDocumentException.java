package com.atypon.nosql.collection;

import com.atypon.nosql.document.Document;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoSuchDocumentException extends RuntimeException {
    public <T extends Document> NoSuchDocumentException(T document) {
        super("Item not found: " + document);
    }
}

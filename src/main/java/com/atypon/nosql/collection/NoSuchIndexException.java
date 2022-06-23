package com.atypon.nosql.collection;

import com.atypon.nosql.document.Document;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoSuchIndexException extends RuntimeException {
    public <T extends Document> NoSuchIndexException(T matchDocument) {
        super("No such index exists: " + matchDocument);
    }
}

package com.atypon.nosql.database.collection;

import com.atypon.nosql.database.document.Document;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class IndexAlreadyExistsException extends RuntimeException {
    public <T extends Document> IndexAlreadyExistsException(T indexFields) {
        super("Index already exists: " + indexFields);
    }
}

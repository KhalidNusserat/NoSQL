package com.atypon.nosql.document;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FieldsDoNotMatchException extends RuntimeException {
    public FieldsDoNotMatchException() {
    }
}

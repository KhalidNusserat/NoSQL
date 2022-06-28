package com.atypon.nosql.collection;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Added document violates schema")
public class DocumentSchemaViolationException extends RuntimeException {
}

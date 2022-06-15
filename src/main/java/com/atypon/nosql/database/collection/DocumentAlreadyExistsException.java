package com.atypon.nosql.database.collection;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Document already exists")
public class DocumentAlreadyExistsException extends RuntimeException {
}

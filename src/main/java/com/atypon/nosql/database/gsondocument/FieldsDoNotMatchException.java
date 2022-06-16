package com.atypon.nosql.database.gsondocument;

import com.atypon.nosql.database.document.Document;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FieldsDoNotMatchException extends RuntimeException {
    public FieldsDoNotMatchException() {
    }

    public FieldsDoNotMatchException(Document first, Document second) {
        super(String.format("Fields do not match between %s and %s", first, second));
    }
}

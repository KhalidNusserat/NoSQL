package com.atypon.nosql.gsondocument;

import com.atypon.nosql.document.Document;

public class FieldsDoNotMatchException extends Exception {
    public FieldsDoNotMatchException() {
    }

    public <T extends Document<?>> FieldsDoNotMatchException(T first, T second) {
        super(String.format("Fields do not match between %s and %s", first, second));
    }
}

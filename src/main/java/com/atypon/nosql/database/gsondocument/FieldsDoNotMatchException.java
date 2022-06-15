package com.atypon.nosql.database.gsondocument;

import com.atypon.nosql.database.document.Document;

public class FieldsDoNotMatchException extends Exception {
    public FieldsDoNotMatchException() {
    }

    public <T extends Document<?>> FieldsDoNotMatchException(T first, T second) {
        super(String.format("Fields do not match between %s and %s", first, second));
    }
}
package com.atypon.nosql.gsondocument.constraints;

import com.google.gson.JsonElement;

public class FieldExistsConstraint implements Constraint {
    private final String field;

    public static FieldExistsConstraint contains(String field) {
        return new FieldExistsConstraint(field);
    }

    public FieldExistsConstraint(String field) {
        this.field = field;
    }

    @Override
    public boolean validate(JsonElement jsonElement) {
        return jsonElement.getAsJsonObject().has(field);
    }
}

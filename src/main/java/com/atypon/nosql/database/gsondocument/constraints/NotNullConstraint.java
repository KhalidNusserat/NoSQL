package com.atypon.nosql.database.gsondocument.constraints;

import com.google.gson.JsonElement;

public class NotNullConstraint implements Constraint {
    public static NotNullConstraint create() {
        return new NotNullConstraint();
    }

    @Override
    public boolean validate(JsonElement jsonElement) {
        return !jsonElement.isJsonNull();
    }
}

package com.atypon.nosql.document.constraints;

import com.google.gson.JsonElement;

public class TypeConstraint implements Constraint {
    private final Class<?> type;

    public TypeConstraint(Class<?> type) {
        this.type = type;
    }

    public static TypeConstraint match(Class<?> type) {
        return new TypeConstraint(type);
    }

    @Override
    public boolean validate(JsonElement jsonElement) {
        if (type == Number.class) {
            return jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isNumber();
        } else if (type == String.class) {
            return jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isString();
        } else if (type == Boolean.class) {
            return jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isBoolean();
        } else {
            return jsonElement.getClass() == type;
        }
    }
}

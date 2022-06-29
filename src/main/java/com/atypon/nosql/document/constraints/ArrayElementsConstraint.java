package com.atypon.nosql.document.constraints;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public class ArrayElementsConstraint implements Constraint {
    private final Constraint constraint;

    public ArrayElementsConstraint(Constraint constraint) {
        this.constraint = constraint;
    }

    public static ArrayElementsConstraint mustMatch(Constraint constraint) {
        return new ArrayElementsConstraint(constraint);
    }

    @Override
    public boolean validate(JsonElement jsonElement) {
        JsonArray array = jsonElement.getAsJsonArray();
        for (JsonElement element : array) {
            if (!constraint.validate(element)) {
                return false;
            }
        }
        return true;
    }
}

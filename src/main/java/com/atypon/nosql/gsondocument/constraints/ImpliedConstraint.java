package com.atypon.nosql.gsondocument.constraints;

import com.google.gson.JsonElement;

public class ImpliedConstraint implements Constraint {
    private final Constraint first;

    private final Constraint second;

    public ImpliedConstraint(Constraint first, Constraint second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean validate(JsonElement jsonElement) {
        if (first.validate(jsonElement)) {
            return second.validate(jsonElement);
        } else {
            return true;
        }
    }
}

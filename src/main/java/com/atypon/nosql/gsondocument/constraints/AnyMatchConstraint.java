package com.atypon.nosql.gsondocument.constraints;

import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;

public class AnyMatchConstraint implements Constraint {
    private final List<Constraint> constraints = new ArrayList<>();

    public static AnyMatchConstraint empty() {
        return new AnyMatchConstraint();
    }

    public void add(Constraint constraint) {
        constraints.add(constraint);
    }

    @Override
    public boolean validate(JsonElement jsonElement) {
        return constraints.stream().anyMatch(constraint -> constraint.validate(jsonElement));
    }
}

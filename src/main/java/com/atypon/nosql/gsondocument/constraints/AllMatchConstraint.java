package com.atypon.nosql.gsondocument.constraints;

import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;

public class AllMatchConstraint implements Constraint {
    private final List<Constraint> constraints = new ArrayList<>();

    public static AllMatchConstraint empty() {
        return new AllMatchConstraint();
    }

    public static AllMatchConstraint of(Constraint... constraints) {
        AllMatchConstraint allMatchConstraint = empty();
        for (Constraint constraint : constraints) {
            allMatchConstraint.add(constraint);
        }
        return allMatchConstraint;
    }

    public void add(Constraint constraint) {
        constraints.add(constraint);
    }

    @Override
    public boolean validate(JsonElement jsonElement) {
        return constraints.stream().allMatch(constraint -> constraint.validate(jsonElement));
    }
}

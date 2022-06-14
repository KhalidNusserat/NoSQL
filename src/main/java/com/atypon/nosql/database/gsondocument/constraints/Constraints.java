package com.atypon.nosql.database.gsondocument.constraints;

import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;

public class Constraints implements Constraint {
    private final List<Constraint> constraints;

    public Constraints(List<Constraint> constraints) {
        this.constraints = constraints;
    }

    public Constraints() {
        constraints = new ArrayList<>();
    }

    public static Constraints empty() {
        return new Constraints();
    }

    public static Constraints of(Constraint... constraints) {
        Constraints allMatchConstraint = empty();
        for (Constraint constraint : constraints) {
            allMatchConstraint.add(constraint);
        }
        return allMatchConstraint;
    }

    public static Constraints of(List<Constraint> constraints) {
        return new Constraints(constraints);
    }

    public void add(Constraint constraint) {
        constraints.add(constraint);
    }

    @Override
    public boolean validate(JsonElement jsonElement) {
        for (Constraint constraint : constraints) {
            if (!constraint.validate(jsonElement)) {
                return false;
            }
        }
        return true;
    }
}

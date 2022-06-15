package com.atypon.nosql.database.gsondocument.constraints;

import com.google.gson.JsonElement;

public interface Constraint {
    boolean validate(JsonElement jsonElement);

    default ImpliedConstraint implies(Constraint otherConstraint) {
        return new ImpliedConstraint(this, otherConstraint);
    }
}
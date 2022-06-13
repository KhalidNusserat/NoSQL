package com.atypon.nosql.gsondocument.constraints;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class FieldConstraint implements Constraint {
    private final String field;

    private final boolean optional;

    private final boolean notNull;

    private final Constraint fieldConstraint;

    private FieldConstraint(String field, boolean optional, boolean notNull, Constraint fieldConstraint) {
        this.field = field;
        this.optional = optional;
        this.notNull = notNull;
        this.fieldConstraint = fieldConstraint;
    }

    public static FieldConstraint from(String field) {
        return new FieldConstraint(field, false, false, Constraints.empty());
    }

    public FieldConstraint isOptional() {
        return new FieldConstraint(field, true, notNull, fieldConstraint);
    }

    public FieldConstraint isNotNull() {
        return new FieldConstraint(field, optional, true, fieldConstraint);
    }

    public FieldConstraint mustMatch(Constraint constraint) {
        return new FieldConstraint(field, optional, notNull, constraint);
    }

    @Override
    public boolean validate(JsonElement element) {
        Preconditions.checkState(element.isJsonObject());
        JsonObject object = element.getAsJsonObject();
        if (!object.has(field)) {
            return optional;
        }
        if (object.get(field).isJsonNull()) {
            return !notNull;
        }
        return fieldConstraint.validate(object.get(field));
    }
}

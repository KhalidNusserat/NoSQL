package com.atypon.nosql.database.gsondocument;

import com.atypon.nosql.database.document.InvalidDocumentSchema;
import com.atypon.nosql.database.gsondocument.constraints.Constraint;
import com.atypon.nosql.database.gsondocument.constraints.TypeConstraint;

public class TypeConstraintParser {
    public Constraint extractTypeConstraint(String type) {
        return switch (type) {
            case "number" -> TypeConstraint.match(Number.class);
            case "string" -> TypeConstraint.match(String.class);
            case "boolean" -> TypeConstraint.match(Boolean.class);
            default -> throw new InvalidDocumentSchema("Unrecognized type: " + type);
        };
    }
}

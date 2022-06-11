package com.atypon.nosql.gsondocument;

import com.atypon.nosql.document.DocumentSchemaGenerator;
import com.atypon.nosql.document.InvalidDocumentSchema;
import com.atypon.nosql.gsondocument.constraints.*;
import com.atypon.nosql.keywordsparser.InvalidKeywordException;
import com.atypon.nosql.keywordsparser.KeywordsParser;
import com.atypon.nosql.keywordsparser.SimpleKeywordsParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.regex.Pattern;

public class GsonDocumentSchemaGenerator implements DocumentSchemaGenerator<GsonDocument> {
    private static final Pattern notNullPattern = Pattern.compile("^.+!\\??$");

    private static final Pattern optionalPattern = Pattern.compile("^.*\\?!?$");

    private static final Pattern validFieldPattern = Pattern.compile("^[^ .?!]+(\\??!?|!?\\??)$");

    private static final ConstraintKeywordTranslator keywordTranslator = new SimpleConstraintKeywordTranslator();

    @Override
    public GsonDocumentSchema createSchema(GsonDocument schemaDocument)
            throws InvalidDocumentSchema, InvalidKeywordException
    {
        return new GsonDocumentSchema(getElementConstraints(schemaDocument.object));
    }

    private Constraint getConstraints(String field, JsonElement element)
            throws InvalidDocumentSchema, InvalidKeywordException
    {
        if (!isFieldValid(field)) {
            throw new InvalidDocumentSchema("Field contains illegal characters");
        }
        AllMatchConstraint constraints = AllMatchConstraint.empty();
        constraints = getFieldConstraints(field, constraints);
        constraints.add(getElementConstraints(element));
        return constraints;
    }

    private boolean isFieldValid(String field) {
        return validFieldPattern.matcher(field).matches();
    }

    private AllMatchConstraint getFieldConstraints(String field, AllMatchConstraint constraints) {
        constraints = createOptionalConstraint(constraints, field);
        constraints = createNotNullConstraint(constraints, field);
        return constraints;
    }

    private AllMatchConstraint createOptionalConstraint(AllMatchConstraint constraints, String field) {
        if (optionalPattern.matcher(field).matches()) {
            AllMatchConstraint newCurrentConstraints = AllMatchConstraint.empty();
            constraints.add(
                    FieldExistsConstraint.contains(field).implies(newCurrentConstraints)
            );
            return newCurrentConstraints;
        }
        constraints.add(FieldExistsConstraint.contains(field));
        return constraints;
    }

    private AllMatchConstraint createNotNullConstraint(AllMatchConstraint constraints, String field) {
        if (!notNullPattern.matcher(field).matches()) {
            AllMatchConstraint newCurrentConstraints = AllMatchConstraint.empty();
            constraints.add(
                    NotNullConstraint.create().implies(newCurrentConstraints)
            );
            return newCurrentConstraints;
        }
        constraints.add(NotNullConstraint.create());
        return constraints;
    }

    private AllMatchConstraint parseConstraintsString(String constraintString)
            throws InvalidKeywordException, InvalidDocumentSchema {
        return AllMatchConstraint.of(keywordTranslator.translate(constraintString));
    }

    private AllMatchConstraint getElementConstraints(JsonElement element)
            throws InvalidDocumentSchema, InvalidKeywordException {
        if (element.isJsonPrimitive()) {
            return parseConstraintsString(element.getAsString());
        } else if (element.isJsonArray()) {
            AllMatchConstraint constraint = AllMatchConstraint.of(TypeConstraint.match(JsonArray.class));
            JsonArray array = element.getAsJsonArray();
            if (array.isEmpty()) {
                throw new InvalidDocumentSchema("Expected a constraint inside the array");
            }
            constraint.add(
                    ArrayElementsConstraint.match(getElementConstraints(array.get(0)))
            );
            return constraint;
        } else if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            AllMatchConstraint constraint = AllMatchConstraint.empty();
            for (var entry : object.entrySet()) {
                constraint.add(
                        getConstraints(entry.getKey(), entry.getValue())
                );
            }
            return constraint;
        } else {
            throw new InvalidDocumentSchema("Null is not expected in a schema document");
        }
    }
}

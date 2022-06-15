package com.atypon.nosql.database.gsondocument;

import com.atypon.nosql.database.document.DocumentSchemaGenerator;
import com.atypon.nosql.database.document.InvalidDocumentSchema;
import com.atypon.nosql.database.gsondocument.constraints.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.regex.Pattern;

public class GsonDocumentSchemaGenerator implements DocumentSchemaGenerator<GsonDocument> {
    private final Pattern optionalPattern = Pattern.compile("^\\w+(!?\\?|\\?!?)$");

    private final Pattern notnullPattern = Pattern.compile("^\\w+(\\??!|!\\??)$");

    private final TypeConstraintParser typeConstraintParser = new TypeConstraintParser();

    @Override
    public GsonDocumentSchema createSchema(GsonDocument schemaDocument) throws InvalidDocumentSchema {
        JsonObject schemaObject = schemaDocument.object;
        return new GsonDocumentSchema(extractConstraintsFromObject(schemaObject), schemaDocument);
    }

    private Constraints extractConstraintsFromObject(JsonObject object) throws InvalidDocumentSchema {
        Constraints constraints = Constraints.empty();
        for (var entry : object.entrySet()) {
            String field = entry.getKey();
            JsonElement element = entry.getValue();
            FieldConstraint fieldConstraint = FieldConstraint
                    .from(cleanupField(field))
                    .mustMatch(extractConstraints(element));
            if (notnullPattern.matcher(field).matches()) {
                fieldConstraint = fieldConstraint.isNotNull();
            }
            if (optionalPattern.matcher(field).matches()) {
                fieldConstraint = fieldConstraint.isOptional();
            }
            constraints.add(fieldConstraint);
        }
        return constraints;
    }

    private String cleanupField(String field) {
        return field.replace("!", "").replace("?", "");
    }

    private Constraint extractConstraints(JsonElement element) throws InvalidDocumentSchema {
        if (element.isJsonNull()) {
            throw new InvalidDocumentSchema("Null not expected in a schema definition");
        } else if (element.isJsonPrimitive()) {
            return typeConstraintParser.extractTypeConstraint(element.getAsString());
        } else if (element.isJsonArray()) {
            return Constraints.of(
                    TypeConstraint.match(JsonArray.class),
                    ArrayElementsConstraint.mustMatch(
                            extractConstraints(element.getAsJsonArray().get(0))
                    )
            );
        } else {
            return Constraints.of(
                    TypeConstraint.match(JsonObject.class),
                    extractConstraintsFromObject(element.getAsJsonObject())
            );
        }
    }
}

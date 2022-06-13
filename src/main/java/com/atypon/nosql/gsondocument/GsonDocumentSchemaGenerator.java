package com.atypon.nosql.gsondocument;

import com.atypon.nosql.document.DocumentSchemaGenerator;
import com.atypon.nosql.document.InvalidDocumentSchema;
import com.atypon.nosql.gsondocument.constraints.*;
import com.atypon.nosql.keywordsparser.InvalidKeywordException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.regex.Pattern;

public class GsonDocumentSchemaGenerator implements DocumentSchemaGenerator<GsonDocument> {
    private final Pattern optionalPattern = Pattern.compile("^\\w+(!?\\?|\\?!?)$");

    private final Pattern notnullPattern = Pattern.compile("^\\w+(\\??!|!\\??)$");

    private final StringConstraintsParser stringConstraintsParser = new StringConstraintsParser();

    @Override
    public GsonDocumentSchema createSchema(GsonDocument schemaDocument)
            throws InvalidDocumentSchema, InvalidKeywordException
    {
        JsonObject schemaObject = schemaDocument.object;
        return new GsonDocumentSchema(extractConstraintsFromObject(schemaObject), schemaDocument);
    }

    private Constraints extractConstraintsFromObject(JsonObject object)
            throws InvalidKeywordException, InvalidDocumentSchema
    {
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

    private Constraint extractConstraints(JsonElement element) throws InvalidDocumentSchema, InvalidKeywordException {
        if (element.isJsonNull()) {
            throw new InvalidDocumentSchema("Null not expected in a schema definition");
        } else if (element.isJsonPrimitive()) {
            return stringConstraintsParser.extractConstraints(element.getAsString());
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

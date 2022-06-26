package com.atypon.nosql.gsondocument;

import com.atypon.nosql.document.InvalidDocumentSchema;
import com.atypon.nosql.gsondocument.constraints.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class ConstraintsExtractor {

    private final Pattern optionalPattern = Pattern.compile("^\\w+(!?\\?|\\?!?)$");

    private final Pattern notnullPattern = Pattern.compile("^\\w+(\\??!|!\\??)$");

    private final TypeConstraintParser typeConstraintParser = new TypeConstraintParser();

    public Constraints extractFromObject(JsonObject object) {
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

    private Constraint extractConstraints(JsonElement element) {
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
                    extractFromObject(element.getAsJsonObject())
            );
        }
    }
}

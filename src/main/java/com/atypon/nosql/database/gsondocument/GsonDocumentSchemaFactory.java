package com.atypon.nosql.database.gsondocument;

import com.atypon.nosql.database.document.Document;
import com.atypon.nosql.database.document.DocumentSchemaFactory;
import com.atypon.nosql.database.document.InvalidDocumentSchema;
import com.atypon.nosql.database.gsondocument.constraints.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Slf4j
@Component
public class GsonDocumentSchemaFactory implements DocumentSchemaFactory {
    private final Pattern optionalPattern = Pattern.compile("^\\w+(!?\\?|\\?!?)$");

    private final Pattern notnullPattern = Pattern.compile("^\\w+(\\??!|!\\??)$");

    private final TypeConstraintParser typeConstraintParser = new TypeConstraintParser();

    @Override
    public GsonDocumentSchema createSchema(Document schemaDocument) {
        GsonDocument gsonDocument = (GsonDocument) schemaDocument;
        JsonObject schemaObject = gsonDocument.object;
        return new GsonDocumentSchema(extractConstraintsFromObject(schemaObject), gsonDocument);
    }

    private Constraints extractConstraintsFromObject(JsonObject object) {
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
            log.error(
                    "Null is not expected in a schema definition document"
            );
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

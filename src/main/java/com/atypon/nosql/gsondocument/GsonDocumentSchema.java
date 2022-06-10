package com.atypon.nosql.gsondocument;

import com.atypon.nosql.keywordsparser.InvalidKeywordException;
import com.atypon.nosql.keywordsparser.SimpleKeywordsParser;
import com.atypon.nosql.schema.DocumentSchema;
import com.google.gson.JsonObject;

import javax.naming.directory.SchemaViolationException;

public class GsonDocumentSchema implements DocumentSchema<GsonDocument> {
    private final GsonObjectSchema objectSchema;

    public GsonDocumentSchema(JsonObject schemaDocumentObject) throws InvalidKeywordException {
        GsonElementSchemaParser parser = new GsonElementSchemaParser(new SimpleKeywordsParser());
        objectSchema = parser.parseObject(schemaDocumentObject);
    }

    @Override
    public GsonDocument validate(GsonDocument document) throws SchemaViolationException {
        GsonDocument validatedDocument = new GsonDocument(objectSchema.validate(document.object).getAsJsonObject());
        validatedDocument.object.addProperty("_id", document.id());
        return validatedDocument;
    }
}

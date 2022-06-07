package com.atypon.nosql.gsondocument;

import com.atypon.nosql.keywordsparser.InvalidKeywordException;
import com.atypon.nosql.keywordsparser.SimpleKeywordsParser;
import com.atypon.nosql.schema.DocumentSchema;

import javax.naming.directory.SchemaViolationException;

public class GsonDocumentSchema implements DocumentSchema<GsonDocument> {
    private final GsonObjectSchema objectSchema;

    public GsonDocumentSchema(GsonObjectSchema objectSchema) {
        this.objectSchema = objectSchema;
    }

    public GsonDocumentSchema(String schemaDescription) throws InvalidKeywordException {
        GsonElementSchemaParser parser = new GsonElementSchemaParser(new SimpleKeywordsParser());
        objectSchema = parser.parse(schemaDescription);
    }

    @Override
    public GsonDocument validate(GsonDocument document) throws SchemaViolationException {
        return new GsonDocument(objectSchema.validate(document.object));
    }
}

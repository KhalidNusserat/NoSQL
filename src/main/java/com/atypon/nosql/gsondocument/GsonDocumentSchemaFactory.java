package com.atypon.nosql.gsondocument;

import com.atypon.nosql.keywordsparser.InvalidKeywordException;
import com.atypon.nosql.schema.DocumentSchema;
import com.atypon.nosql.schema.DocumentSchemaFactory;
import com.google.gson.JsonObject;

public class GsonDocumentSchemaFactory implements DocumentSchemaFactory<GsonDocument> {
    @Override
    public DocumentSchema<GsonDocument> create(GsonDocument schemaDocument) throws InvalidKeywordException {
        JsonObject schemaDocumentObject = schemaDocument.object.deepCopy();
        schemaDocumentObject.remove("_id");
        return new GsonDocumentSchema(schemaDocumentObject);
    }
}

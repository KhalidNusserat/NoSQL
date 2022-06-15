package com.atypon.nosql.database.gsondocument;

import com.atypon.nosql.database.document.DocumentGenerator;
import com.atypon.nosql.database.document.ObjectIdGenerator;
import com.google.gson.JsonPrimitive;

public class GsonDocumentGenerator implements DocumentGenerator<GsonDocument> {
    private final ObjectIdGenerator idGenerator;

    public GsonDocumentGenerator(ObjectIdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    public GsonDocument createFromString(String src) {
        return GsonDocument.fromString(src);
    }

    @Override
    public GsonDocument appendId(GsonDocument document) {
        JsonPrimitive idElement = new JsonPrimitive(idGenerator.getNewId());
        return (GsonDocument) document.withField("_id", idElement);
    }
}
package com.atypon.nosql.gsondocument;

import com.atypon.nosql.document.DocumentGenerator;
import com.atypon.nosql.document.ObjectIdGenerator;
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

    public GsonDocument appendId(GsonDocument document) {
        JsonPrimitive idElement = new JsonPrimitive(idGenerator.getNewId());
        return (GsonDocument) document.withField("_id", idElement);
    }
}

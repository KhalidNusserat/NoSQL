package com.atypon.nosql.database.gsondocument;

import com.atypon.nosql.database.document.Document;
import com.atypon.nosql.database.document.DocumentFactory;
import com.atypon.nosql.database.document.ObjectIdGenerator;

import java.util.Map;

public class GsonDocumentFactory implements DocumentFactory {
    private final ObjectIdGenerator idGenerator;

    public GsonDocumentFactory(ObjectIdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    public GsonDocument createFromString(String src) {
        return GsonDocument.fromString(src);
    }

    @Override
    public Document createFromMap(Map<String, Object> map) {
        return GsonDocument.fromMap(map);
    }

    @Override
    public GsonDocument appendId(Document document) {
        GsonDocument originalDocument = (GsonDocument) document;
        GsonDocument resultDocument = GsonDocument.fromJsonObject(originalDocument.object);
        resultDocument.object.addProperty("_id", idGenerator.getNewId());
        return resultDocument;
    }
}

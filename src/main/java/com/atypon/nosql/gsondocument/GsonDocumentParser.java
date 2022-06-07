package com.atypon.nosql.gsondocument;

import com.atypon.nosql.document.DocumentParser;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class GsonDocumentParser implements DocumentParser<GsonDocument> {
    private final Gson gson = new Gson();

    @Override
    public GsonDocument parse(String src) {
        JsonObject object = gson.fromJson(src, JsonObject.class);
        GsonDocument document = new GsonDocument(object);
        document.object.addProperty("_id", object.get("_id").getAsString());
        return document;
    }
}

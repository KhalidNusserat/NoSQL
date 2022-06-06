package com.atypon.nosql.gsondocument;

import com.atypon.nosql.document.DocumentParser;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class GsonDocumentParser implements DocumentParser<GsonDocument> {
    private final Gson gson = new Gson();

    @Override
    public GsonDocument parse(String src) {
        return new GsonDocument(gson.fromJson(src, JsonObject.class));
    }
}

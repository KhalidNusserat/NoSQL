package com.atypon.nosql.gsondocument;

import com.google.gson.JsonObject;

public class GsonMatchDocument {
    public static GsonDocument newGsonMatchDocument(JsonObject object, boolean matchID) {
        GsonDocument document = new GsonDocument(object);
        document.object.addProperty("_matchID", matchID);
        if (object.has("_id")) {
            document.object.add("_id", object.get("_id"));
        }
        return document;
    }
}

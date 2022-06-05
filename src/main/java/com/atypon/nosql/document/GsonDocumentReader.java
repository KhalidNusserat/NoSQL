package com.atypon.nosql.document;

import com.atypon.nosql.gsonwrapper.GsonObject;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class GsonDocumentReader implements DocumentReader<JsonElement> {
    @Override
    public Document<JsonElement> read(String path) throws IOException {
        Preconditions.checkNotNull(path);
        Gson gson = new Gson();
        return new GsonObject(gson.toJsonTree(Files.readString(Path.of(path))));
    }
}

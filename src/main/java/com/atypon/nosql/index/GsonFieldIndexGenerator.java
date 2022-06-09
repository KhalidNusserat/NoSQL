package com.atypon.nosql.index;

import com.atypon.nosql.document.DocumentField;
import com.atypon.nosql.gsondocument.GsonDocument;
import com.atypon.nosql.utils.ReversedMap;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import java.util.Set;

public class GsonFieldIndexGenerator implements FieldIndexGenerator<GsonDocument> {
    private final Gson gson;

    private final Random random = new Random();

    public GsonFieldIndexGenerator(Gson gson) {
        this.gson = gson;
    }

    @Override
    public GsonFieldIndex read(Path indexPath) {
        try (BufferedReader reader = Files.newBufferedReader(indexPath)) {
            JsonObject object = gson.fromJson(reader, JsonObject.class);
            Set<DocumentField> documentFields = gson.fromJson(
                    object.get("_fields"),
                    new TypeToken<Set<DocumentField>>(){}.getType()
            );
            ReversedMap<Path, Set<JsonElement>> valuesToPaths = gson.fromJson(
                    object.get("_content"),
                    new TypeToken<ReversedMap<Path, Set<JsonElement>>>(){}.getType()
            );
            return new GsonFieldIndex(documentFields, indexPath, gson, valuesToPaths);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't read index file: " + indexPath);
        }
    }

    @Override
    public GsonFieldIndex create(Set<DocumentField> documentFields, Path directoryPath) {
        return new GsonFieldIndex(documentFields, directoryPath.resolve(random.nextLong() + ".json"), gson);
    }
}

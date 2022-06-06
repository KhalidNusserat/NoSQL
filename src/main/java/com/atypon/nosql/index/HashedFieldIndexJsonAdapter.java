package com.atypon.nosql.index;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

public class HashedFieldIndexJsonAdapter<K, V> implements
        JsonSerializer<HashedFieldIndex<K, V>>,
        JsonDeserializer<HashedFieldIndex<K, V>> {
    @Override
    public JsonElement serialize(HashedFieldIndex<K, V> src, Type typeOfSrc, JsonSerializationContext context) {
        Gson gson = new Gson();
        JsonObject object = new JsonObject();
        object.add("keyToValue", gson.toJsonTree(src.keyToValue, new TypeToken<Map<K, V>>(){}.getType()));
        object.add("valueToKeys", gson.toJsonTree(src.valueToKeys, new TypeToken<Map<V, Set<K>>>(){}.getType()));
        return object;
    }

    @Override
    public HashedFieldIndex<K, V> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Gson gson = new Gson();
        HashedFieldIndex<K, V> index = new HashedFieldIndex<>();
        return new HashedFieldIndex<>(
                gson.fromJson(
                        json.getAsJsonObject().get("keyToValue"),
                        new TypeToken<Map<K, V>>(){}.getType()
                ),
                gson.fromJson(
                        json.getAsJsonObject().get("valueToKeys"),
                        new TypeToken<Map<V, Set<K>>>(){}.getType()
                )
        );
    }
}

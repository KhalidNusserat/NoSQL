package com.atypon.nosql.document;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.Map;

public class DocumentJacksonDeserializer extends JsonDeserializer<Document> {

    @Override
    public Document deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {
        JavaType mapType = deserializationContext.getTypeFactory().constructMapType(
                Map.class,
                String.class,
                Object.class
        );
        Map<String, Object> documentMap = deserializationContext.readValue(jsonParser, mapType);
        return Document.fromMap(documentMap);
    }
}

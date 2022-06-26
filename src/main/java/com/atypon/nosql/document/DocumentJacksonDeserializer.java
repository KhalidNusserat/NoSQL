package com.atypon.nosql.document;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.Map;

public class DocumentJacksonDeserializer extends JsonDeserializer<Document> {

    @SuppressWarnings("unchecked")
    @Override
    public Document deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {
        Map<String, Object> documentMap = jsonParser.readValueAs(Map.class);
        return Document.fromMap(documentMap);
    }
}

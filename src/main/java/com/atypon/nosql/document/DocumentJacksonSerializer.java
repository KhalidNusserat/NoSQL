package com.atypon.nosql.document;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class DocumentJacksonSerializer extends JsonSerializer<Document> {
    @Override
    public void serialize(Document value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeObject(value.toMap());
    }
}

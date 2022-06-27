package com.atypon.nosql.jacksondocument;

import com.atypon.nosql.document.Document;
import com.atypon.nosql.document.FieldsDoNotMatchException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;

public class JacksonDocument extends Document {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final TypeReference<Map<String, Object>> mapType = new TypeReference<>() {
    };

    private final ObjectNode node;

    public JacksonDocument(ObjectNode node) {
        this.node = node;
    }

    @Override
    public boolean subsetOf(Document matchDocument) {
        JacksonDocument jacksonDocument = (JacksonDocument) matchDocument;
        return firstSubsetOfSecond(node, jacksonDocument.node);
    }

    private boolean firstSubsetOfSecond(JsonNode first, JsonNode second) {
        if (!first.getNodeType().equals(second.getNodeType())) {
            return false;
        } else if (first.isArray() || isPrimitive(first) || first.isNull()) {
            return first.equals(second);
        }
        var iterator = node.fields();
        while (iterator.hasNext()) {
            var entry = iterator.next();
            String field = entry.getKey();
            JsonNode value = entry.getValue();
            if (!second.has(field)) {
                return false;
            } else if (!firstSubsetOfSecond(value, second.get(field))) {
                return false;
            }
        }
        return true;
    }

    private boolean isPrimitive(JsonNode node) {
        return node.isNumber() || node.isBoolean() || node.isTextual();
    }

    @Override
    public Document getValues(Document otherDocument) {
        JacksonDocument jacksonDocument = (JacksonDocument) otherDocument;
        return new JacksonDocument(getValues(jacksonDocument.node, node));
    }

    private ObjectNode getValues(JsonNode fieldsSource, JsonNode valuesSource) {
        ObjectNode objectNode = new ObjectNode(JsonNodeFactory.instance);
        var iterator = fieldsSource.fields();
        while (iterator.hasNext()) {
            var entry = iterator.next();
            String field = entry.getKey();
            JsonNode value = entry.getValue();
            if (!value.isObject()) {
                objectNode.set(field, value);
            } else {
                if (!valuesSource.has(field)) {
                    throw new FieldsDoNotMatchException();
                }
                objectNode.set(field, getValues(value, valuesSource.get(field)));
            }
        }
        return objectNode;
    }

    @Override
    public Document getFields() {
        return new JacksonDocument(getFields(node));
    }

    public ObjectNode getFields(JsonNode objectNode) {
        ObjectNode result = new ObjectNode(JsonNodeFactory.instance);
        var iterator = objectNode.fields();
        while (iterator.hasNext())  {
            var entry = iterator.next();
            String field = entry.getKey();
            JsonNode value = entry.getValue();
            if (value.isArray() || value.isNull() || isPrimitive(value)) {
                result.set(field, NullNode.instance);
            } else {
                result.set(field, getFields(value));
            }
        }
        return result;
    }

    @Override
    public Document overrideFields(Document newFieldsValues) {
        return null;
    }

    @Override
    public Map<String, Object> toMap() {
        try {
            return objectMapper.readValue(node.traverse(), mapType);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public <T> T toObject(Class<T> classOfObject) {
        try {
            return objectMapper.readValue(node.traverse(), classOfObject);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public Document withId() {
        ObjectNode withId = node.deepCopy();
        withId.put("_id", idGenerator.newId(node));
        return new JacksonDocument(withId);
    }
}

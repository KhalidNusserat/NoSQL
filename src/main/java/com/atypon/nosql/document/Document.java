package com.atypon.nosql.document;

import com.atypon.nosql.idgenerator.IdGenerator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.HashMap;
import java.util.Map;

@JsonDeserialize(using = DocumentJacksonDeserializer.class)
@JsonSerialize(using = DocumentJacksonSerializer.class)
public abstract class Document {

    protected static IdGenerator idGenerator;

    private static DocumentFactory documentFactory;

    public static void setDocumentFactory(DocumentFactory documentFactory) {
        Document.documentFactory = documentFactory;
    }

    public static void setIdGenerator(IdGenerator idGenerator) {
        Document.idGenerator = idGenerator;
    }

    public static Document fromJson(String json) {
        return documentFactory.createFromJson(json);
    }

    public static Document fromMap(Map<String, Object> map) {
        return documentFactory.createFromMap(map);
    }

    public static Document fromObject(Object object) {
        return documentFactory.createFromObject(object);
    }

    public static Document of(Object... elements) {
        Map<String, Object> result = new HashMap<>();
        for (int i = 0; i < elements.length - 1; i += 2) {
            if (elements[i] instanceof String field) {
                result.put(field, elements[i + 1]);
            } else {
                throw new IllegalArgumentException("Field must be a string, instead got: " + elements[i]);
            }
        }
        return fromMap(result);
    }

    public abstract boolean subsetOf(Document otherDocument);

    public abstract Document getValues(Document fields);

    public abstract Document getFields();

    public abstract Document overrideFields(Document newFieldsValues);

    public abstract Map<String, Object> toMap();

    public abstract <T> T toObject(Class<T> classOfObject);

    public abstract Document withId();
}

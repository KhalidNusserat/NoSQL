package com.atypon.nosql.document;

import com.atypon.nosql.idgenerator.IdGenerator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Map;

@JsonDeserialize(using = DocumentJacksonDeserializer.class)
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

    public abstract boolean subsetOf(Document matchDocument);

    public abstract Document getValuesToMatch(Document otherDocument);

    public abstract Document getFields();

    public abstract Document overrideFields(Document newFieldsValues);

    public abstract Map<String, Object> toMap();

    public abstract <T> T toObject(Class<T> classOfObject);

    public abstract Document withId();
}

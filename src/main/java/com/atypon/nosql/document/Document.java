package com.atypon.nosql.document;

import java.util.Map;

public abstract class Document {

    private static DocumentFactory documentFactory;

    public abstract boolean subsetOf(Document matchDocument);

    public abstract Document getValuesToMatch(Document otherDocument);

    public abstract Document getFields();

    public abstract Document overrideFields(Document newFieldsValues);

    public abstract Map<String, Object> toMap();

    public abstract <T> T toObject(Class<T> classOfObject);

    public static void setDocumentFactory(DocumentFactory documentFactory) {
        Document.documentFactory = documentFactory;
    }

    public static Document createFromJson(String json) {
        return documentFactory.createFromJson(json);
    }

    public static Document createFromMap(Map<String, Object> map) {
        return documentFactory.createFromMap(map);
    }
}

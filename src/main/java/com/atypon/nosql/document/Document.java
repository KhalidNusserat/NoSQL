package com.atypon.nosql.document;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public abstract class Document {

    private static DocumentFactory documentFactory;

    @Autowired
    public final void setDocumentFactory(DocumentFactory documentFactory) {
        Document.documentFactory = documentFactory;
    }

    public abstract boolean subsetOf(Document matchDocument);

    public abstract Document getValuesToMatch(Document otherDocument);

    public abstract Document getFields();

    public abstract Document overrideFields(Document newFieldsValues);

    public abstract Map<String, Object> toMap();

    public abstract <T> T toObject(Class<T> classOfObject);

    public static Document createFromJson(String json) {
        return documentFactory.createFromJson(json);
    }

    public static Document createFromMap(Map<String, Object> map) {
        return documentFactory.createFromMap(map);
    }
}

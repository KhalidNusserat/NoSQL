package com.atypon.nosql.services;

import com.atypon.nosql.database.document.Document;
import com.atypon.nosql.database.document.DocumentFactory;
import com.atypon.nosql.database.document.IdGenerator;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DefaultDocumentTranslator implements DocumentTranslator {
    private final DocumentFactory documentFactory;

    private final IdGenerator idGenerator;

    public DefaultDocumentTranslator(DocumentFactory documentFactory, IdGenerator idGenerator) {
        this.documentFactory = documentFactory;
        this.idGenerator = idGenerator;
    }

    @Override
    public Document translate(Map<String, Object> map) {
        Document document = documentFactory.createFromMap(map);
        if (!document.containsField("_id")) {
            document = document.withField("_id", idGenerator.newId(document));
        }
        return document;
    }
}

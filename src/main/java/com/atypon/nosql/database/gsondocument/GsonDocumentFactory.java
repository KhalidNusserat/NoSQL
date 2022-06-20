package com.atypon.nosql.database.gsondocument;

import com.atypon.nosql.database.document.Document;
import com.atypon.nosql.database.document.DocumentFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GsonDocumentFactory implements DocumentFactory {

    @Override
    public GsonDocument createFromString(String src) {
        return GsonDocument.fromString(src);
    }

    @Override
    public Document createFromMap(Map<String, Object> map) {
        return GsonDocument.fromMap(map);
    }
}

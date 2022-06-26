package com.atypon.nosql.gsondocument;

import com.atypon.nosql.document.Document;
import com.atypon.nosql.document.DocumentFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GsonDocumentFactory implements DocumentFactory {

    @Override
    public GsonDocument createFromJson(String src) {
        return GsonDocument.fromJson(src);
    }

    @Override
    public Document createFromMap(Map<String, Object> map) {
        return GsonDocument.fromMap(map);
    }
}

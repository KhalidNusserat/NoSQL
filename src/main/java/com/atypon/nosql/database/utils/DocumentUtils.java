package com.atypon.nosql.database.utils;

import com.atypon.nosql.database.document.Document;

import java.util.Collection;
import java.util.Map;

public class DocumentUtils {
    public static Collection<Map<String, Object>> documentsToMaps(Collection<Document> documents) {
        return documents.stream().map(Document::getAsMap).toList();
    }
}

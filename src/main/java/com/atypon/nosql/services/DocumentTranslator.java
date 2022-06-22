package com.atypon.nosql.services;

import com.atypon.nosql.database.document.Document;

import java.util.Map;

public interface DocumentTranslator {
    Document translate(Map<String, Object> map);
}

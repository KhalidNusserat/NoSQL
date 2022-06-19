package com.atypon.nosql.database.document;

import java.util.Map;

public interface DocumentFactory {
    Document createFromString(String src);

    Document createFromMap(Map<String, Object> map);
}

package com.atypon.nosql.document;

import java.util.Map;

public interface DocumentFactory {
    Document createFromJson(String src);

    Document createFromMap(Map<String, Object> map);
}

package com.atypon.nosql.databaseresponse;

import java.util.Collection;
import java.util.Map;

public record DatabaseResponse(
    String message,
    Collection<Map<String, Object>> result
) {
    public static DatabaseResponse createDatabaseResponse(String message, Collection<Map<String, Object>> documents) {
        return new DatabaseResponse(message, documents);
    }
}
